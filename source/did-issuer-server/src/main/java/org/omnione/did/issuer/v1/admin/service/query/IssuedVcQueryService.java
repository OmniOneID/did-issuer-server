package org.omnione.did.issuer.v1.admin.service.query;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.Vc;
import org.omnione.did.base.db.repository.VcRepository;
import org.omnione.did.issuer.v1.admin.dto.IssuedVcDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description...
 */
@RequiredArgsConstructor
@Service
public class IssuedVcQueryService {
    private final VcRepository vcRepository;

    public Page<IssuedVcDto> searchIssueProfileList(String searchKey, String searchValue, Pageable pageable) {
        Page<Vc> entityPage = vcRepository.searchIssuedVc(searchKey, searchValue, pageable);
        List<IssuedVcDto> vcs = entityPage.stream().map(IssuedVcDto::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(vcs, pageable, entityPage.getTotalElements());
    }
}
