package org.omnione.did.issuer.v1.admin.controller;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.issuer.v1.admin.dto.IssuedVcDto;
import org.omnione.did.issuer.v1.admin.service.IssuedVcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description...
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(UrlConstant.Admin.V1 + UrlConstant.Admin.ISSUED_VCS)
public class IssuedVcAdminController {
    private final IssuedVcService issuedVcService;
    @GetMapping
    public Page<IssuedVcDto> searchIssuedVcList(String searchKey, String searchValue, Pageable pageable){

        return issuedVcService.searchIssuedVcList(searchKey, searchValue, pageable);
    }

    @GetMapping(UrlConstant.Admin.PATH_VARIABLE_ID)
    public ResponseEntity<IssuedVcDto> getIssuedVcById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(issuedVcService.findById(id));
    }

}
