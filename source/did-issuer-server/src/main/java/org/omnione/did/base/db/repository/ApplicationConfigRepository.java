package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.ApplicationConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Description...
 */
public interface ApplicationConfigRepository extends JpaRepository<ApplicationConfig, Long> {
    Optional<ApplicationConfig> findFirstBy();

}
