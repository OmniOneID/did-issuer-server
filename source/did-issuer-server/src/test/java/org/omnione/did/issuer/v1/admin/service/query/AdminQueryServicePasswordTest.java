package org.omnione.did.issuer.v1.admin.service.query;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.db.domain.Admin;
import org.omnione.did.base.db.repository.AdminRepository;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminQueryServicePasswordTest {
    private static final String CLIENT_HASH = "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8";

    @Test
    void legacySha256IsRehashedToBcryptAfterSuccessfulLogin() {
        AdminRepository repository = mock(AdminRepository.class);
        PasswordEncoder encoder = new DelegatingPasswordEncoder("bcrypt", Map.of("bcrypt", new BCryptPasswordEncoder(4)));
        Admin admin = Admin.builder().loginPassword(CLIENT_HASH).passwordAlgo("sha256").build();
        when(repository.findByLoginId("admin")).thenReturn(Optional.of(admin));
        when(repository.save(admin)).thenReturn(admin);
        AdminQueryService service = new AdminQueryService(repository, encoder);

        assertSame(admin, service.findByLoginIdAndLoginPassword("admin", CLIENT_HASH));
        assertTrue(admin.getLoginPassword().startsWith("{bcrypt}"));
        assertEquals("bcrypt", admin.getPasswordAlgo());
        assertTrue(encoder.matches(CLIENT_HASH, admin.getLoginPassword()));
        verify(repository).save(admin);

        assertSame(admin, service.findByLoginIdAndLoginPassword("admin", CLIENT_HASH));
        verify(repository, times(1)).save(admin);
        assertThrows(OpenDidException.class,
                () -> service.findByLoginIdAndLoginPassword("admin", "wrong"));
    }

    @Test
    void wrongLegacyPasswordDoesNotChangeStoredHash() {
        AdminRepository repository = mock(AdminRepository.class);
        PasswordEncoder encoder = new DelegatingPasswordEncoder("bcrypt", Map.of("bcrypt", new BCryptPasswordEncoder(4)));
        Admin admin = Admin.builder().loginPassword(CLIENT_HASH).passwordAlgo("sha256").build();
        when(repository.findByLoginId("admin")).thenReturn(Optional.of(admin));

        assertThrows(OpenDidException.class,
                () -> new AdminQueryService(repository, encoder).findByLoginIdAndLoginPassword("admin", "wrong"));
        assertEquals(CLIENT_HASH, admin.getLoginPassword());
        verify(repository, never()).save(any());
    }
}
