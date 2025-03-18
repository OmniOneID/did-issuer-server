package org.omnione.did.issuer.v1.admin.service;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.issuer.v1.admin.dto.CreateUserInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.UserDto;
import org.omnione.did.issuer.v1.admin.service.query.UserInfoQueryService;
import org.omnione.did.issuer.v1.agent.service.query.UserQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Description...
 */
@RequiredArgsConstructor
@Service
public class UserManagementService {
    private final UserInfoQueryService userQueryService;
    public Page<UserDto> searchUserInfoList(String searchKey, String searchValue, Pageable pageable) {
        return userQueryService.searchUserInfoList(searchKey, searchValue, pageable);
    }

    public void createUserInfo(CreateUserInfoReqDto request) {
        userQueryService.save(User.builder()
                .did(request.getDid())
                .pii(request.getFirstName()+request.getLastName()) // TODO : Trans PII
                .data(request.getUserInfo())
                .vcSchemaId(request.getVcSchemaId())
                .build());
    }
}
