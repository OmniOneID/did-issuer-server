package org.omnione.did.base.property;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtPropertyTest {
    @Test
    void rejectsShortAdminSigningKeyBeforeLogin() {
        JwtProperty property = new JwtProperty();
        property.setSecret("samplepasswordSAMPLEPASSWORD");
        assertThrows(IllegalStateException.class, property::validateSecret);

        property.setSecret("opendidAdminJWTsecretPasswordSample");
        assertDoesNotThrow(property::validateSecret);
    }
}
