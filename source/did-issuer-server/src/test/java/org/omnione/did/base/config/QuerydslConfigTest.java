package org.omnione.did.base.config;

import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.omnione.did.base.db.domain.TestSimpleAuditingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Check the action of "OpenFeign/querydsl"
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
class QuerydslConfigTest {

    @Autowired
    private JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldSameEntityManagerAndJPAQueryFactory() {

        entityManager.persist(new TestSimpleAuditingEntity("Alice"));
        entityManager.persist(new TestSimpleAuditingEntity("Bob"));
        entityManager.persist(new TestSimpleAuditingEntity("Charlie"));

        PathBuilder<TestSimpleAuditingEntity> entity = new PathBuilder<>(TestSimpleAuditingEntity.class, "testSimpleAuditingEntity");

        Long countByEntityManager = (Long) entityManager.createQuery("select count(u.id) from TestSimpleAuditingEntity u").getSingleResult();
        Long countByQueryDsl = queryFactory.select(entity.count()).from(entity).fetchOne();
        assertThat(countByQueryDsl).isEqualTo(countByEntityManager);
    }
}
