package org.omnione.did.base.db.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.omnione.did.InMemoryDataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The BaseEntityTest class contains unit tests for verifying the behavior of
 * entity auditing fields such as `createdAt` and `updatedAt` during persistence.
 *
 * @author birariro
 */
@InMemoryDataJpaTest
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
