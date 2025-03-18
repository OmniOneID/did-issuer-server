package org.omnione.did.issuer.v1.admin.service;

import lombok.RequiredArgsConstructor;
import org.omnione.did.issuer.v1.admin.dto.IssuedVcDto;
import org.omnione.did.issuer.v1.admin.service.query.IssuedVcQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Description...
 */
@RequiredArgsConstructor
@Service
public class IssuedVcService {
    private final IssuedVcQueryService issuedVcQueryService;
    public Page<IssuedVcDto> searchIssueProfileList(String searchKey, String searchValue, Pageable pageable) {
        return issuedVcQueryService.searchIssueProfileList(searchKey, searchValue, pageable);
    }
}
