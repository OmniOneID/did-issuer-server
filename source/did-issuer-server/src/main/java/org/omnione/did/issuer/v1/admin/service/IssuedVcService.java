package org.omnione.did.issuer.v1.admin.service;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.Vc;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.issuer.v1.admin.dto.IssuedVcDto;
import org.omnione.did.issuer.v1.admin.service.query.IssuedVcQueryService;
import org.omnione.did.issuer.v1.agent.service.StorageService;
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
    private final StorageService storageService;

    public Page<IssuedVcDto> searchIssuedVcList(String searchKey, String searchValue, Pageable pageable) {
        return issuedVcQueryService.searchIssueProfileList(searchKey, searchValue, pageable);
    }

    public IssuedVcDto findById(Long id) {
        Vc vc = issuedVcQueryService.findById(id);

        VcMeta vcMetaByVcId = storageService.getVcMetaByVcId(vc.getVcId());

        return IssuedVcDto.fromEntity(vc, vcMetaByVcId.getCredentialSchema().getId(), vcMetaByVcId.getStatus());
    }
}
