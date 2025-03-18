package org.omnione.did.issuer.v1.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.issuer.v1.admin.dto.CreateUserInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.NamespaceDto;
import org.omnione.did.issuer.v1.admin.dto.UserDto;
import org.omnione.did.issuer.v1.admin.service.UserManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Description...
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Admin.V1 + UrlConstant.Admin.USER)
public class UserManagementController {
    private final UserManagementService userManagementService;
    @GetMapping
    public Page<UserDto> searchUserInfoList(String searchKey, String searchValue, Pageable pageable) {
        return userManagementService.searchUserInfoList(searchKey, searchValue, pageable);
    }

    @PostMapping
    public void createUserInfo(@RequestBody CreateUserInfoReqDto request) {
        userManagementService.createUserInfo(request);
    }
}
