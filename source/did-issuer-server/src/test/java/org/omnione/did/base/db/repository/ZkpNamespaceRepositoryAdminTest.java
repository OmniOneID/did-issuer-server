package org.omnione.did.base.db.repository;

import org.junit.jupiter.api.Test;
import org.omnione.did.InMemoryDataJpaTest;
import org.omnione.did.base.config.JpaConfig;
import org.omnione.did.base.config.QuerydslConfig;
import org.omnione.did.base.db.domain.ZkpNamespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@InMemoryDataJpaTest
class ZkpNamespaceRepositoryAdminTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = ZkpNamespace.class)
    @EnableJpaRepositories(basePackageClasses = ZkpNamespaceRepository.class)
    @Import({JpaConfig.class, QuerydslConfig.class})
    static class TestConfiguration {
    }

    @Autowired
    private ZkpNamespaceRepository repository;

    @Test
    void searchesNamespaceIdBySubstring() {
        repository.save(ZkpNamespace.builder().namespaceId("org.example.identity").name("Identity").build());
        repository.save(ZkpNamespace.builder().namespaceId("org.example.address").name("Address").build());

        Page<ZkpNamespace> result = repository.searchNamespaces("namespaceId", "IDENTITY", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).extracting(ZkpNamespace::getNamespaceId)
                .containsExactly("org.example.identity");
    }
}
