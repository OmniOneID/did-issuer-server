package org.omnione.did.base.db.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.omnione.did.base.config.JpaConfig;
import org.omnione.did.base.config.QuerydslConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The BaseEntityTest class contains unit tests for verifying the behavior of
 * entity auditing fields such as `createdAt` and `updatedAt` during persistence.
 *
 * @author birariro
 */
@TestPropertySource(properties = {
        "spring.datasource.url = jdbc:h2:mem:test",
        "spring.datasource.driverClassName = org.h2.Driver",
        "spring.datasource.username = sa",
        "spring.datasource.password = ",
})
@DataJpaTest
@Import({JpaConfig.class, QuerydslConfig.class})
class BaseEntityTest {

    @PersistenceContext
    EntityManager entityManager;

    @Test
    void shouldSetAuditingFieldsOnPersist() {

        TestSimpleAuditingEntity testSimpleAuditingEntity = new TestSimpleAuditingEntity();
        assertNull(testSimpleAuditingEntity.getCreatedAt());
        assertNull(testSimpleAuditingEntity.getUpdatedAt());

        entityManager.persist(testSimpleAuditingEntity);
        assertNotNull(testSimpleAuditingEntity.getCreatedAt());
        assertNotNull(testSimpleAuditingEntity.getUpdatedAt());
    }
}
