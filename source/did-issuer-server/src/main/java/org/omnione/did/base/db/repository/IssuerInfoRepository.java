package org.omnione.did.base.db.repository;

import org.omnione.did.base.db.domain.IssuerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Description...
 */
public interface IssuerInfoRepository extends JpaRepository<IssuerInfo, Long> {
    Optional<IssuerInfo> findFirstBy();

    Optional<IssuerInfo> findTop1ByOrderByIdAsc();
}
