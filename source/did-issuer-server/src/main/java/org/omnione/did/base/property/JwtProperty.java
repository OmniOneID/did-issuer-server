package org.omnione.did.base.property;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperty {
    private String secret;
    private long accessTtlSeconds;
    private long refreshTtlSeconds;

    @PostConstruct
    public void validateSecret() {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalStateException("ADMIN_JWT_SECRET must be at least 32 UTF-8 bytes; generate a unique value with openssl rand -hex 32");
        }
    }
}
