package org.omnione.did.base.db.domain;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.omnione.did.base.config.JpaConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestPropertySource(properties = {
        "spring.datasource.url = jdbc:h2:mem:test",
        "spring.datasource.driverClassName = org.h2.Driver",
        "spring.datasource.username = sa",
        "spring.datasource.password = ",
})
@DataJpaTest
@Import(JpaConfig.class)
class BaseEntityTest {

    @Autowired
    EntityManager entityManager;

    @Test
    void shouldSetAuditingFieldsOnPersist() {

        CertificateVc auditableEntity = CertificateVc.builder().vc("").build();
        assertNull(auditableEntity.getCreatedAt());
        assertNull(auditableEntity.getUpdatedAt());

        entityManager.persist(auditableEntity);
        assertNotNull(auditableEntity.getCreatedAt());
        assertNotNull(auditableEntity.getUpdatedAt());
    }
}
