package org.omnione.did.issuer.v1.admin.service;

import org.omnione.did.issuer.v1.admin.dto.admin.AdminDto;
import org.omnione.did.base.property.JwtProperty;
import org.omnione.did.base.db.repository.AdminRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
@Component
public class JwtService {
    private final JwtProperty props;
    private final AdminRepository adminRepository;

    private Long sessionVersion(Long adminId) {
        return adminRepository.findById(adminId)
                .map(admin -> admin.getSessionVersion())
                .orElseThrow(() -> new IllegalArgumentException("Unknown administrator"));
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(props.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(AdminDto admin) {
        Instant now = Instant.now();
        return Jwts.builder().subject(admin.getId().toString())
                .claim("typ", "access")
                .claim("sessionVersion", sessionVersion(admin.getId()))
                .claim("loginId", admin.getLoginId())
                .claim("role", admin.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(props.getAccessTtlSeconds())))
                .signWith(key()).compact();
    }

    public String createRefreshToken(Long adminId) {
        Instant now = Instant.now();
        return Jwts.builder().subject(adminId.toString())
                .claim("typ", "refresh")
                .claim("sessionVersion", sessionVersion(adminId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(props.getRefreshTtlSeconds())))
                .signWith(key()).compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token);
    }

    public boolean isRefreshToken(Jws<Claims> token) {
        return "refresh".equals(token.getPayload().get("typ", String.class));
    }

    public boolean isCurrentSession(Jws<Claims> token) {
        Long adminId = Long.valueOf(token.getPayload().getSubject());
        Number version = token.getPayload().get("sessionVersion", Number.class);
        return version != null && version.longValue() == sessionVersion(adminId);
    }

    public void revokeAllSessions(Long adminId) {
        if (adminRepository.revokeSessions(adminId) != 1) {
            throw new IllegalArgumentException("Unknown administrator");
        }
    }
}
