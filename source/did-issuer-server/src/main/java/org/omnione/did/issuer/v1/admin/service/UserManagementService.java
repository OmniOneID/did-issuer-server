package org.omnione.did.issuer.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.issuer.v1.admin.dto.CreateUserInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.UserDto;
import org.omnione.did.issuer.v1.admin.service.query.UserInfoQueryService;
import org.omnione.did.issuer.v1.admin.service.query.VcSchemaQueryService;
import org.omnione.did.issuer.v1.agent.service.query.UserQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Description...
 */
@Transactional
@RequiredArgsConstructor
@Service
public class UserManagementService {
    private final UserInfoQueryService userQueryService;
    private final VcSchemaQueryService vcSchemaQueryService;
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

    public UserDto findById(Long id) {
        User user = userQueryService.findById(id);
        VcSchema vcSchema = vcSchemaQueryService.findById(user.getVcSchemaId());

        return UserDto.fromEntity(user, vcSchema.getVcSchemaId());
    }

    public void updateUserInfo(CreateUserInfoReqDto request) {
        User user = userQueryService.findById(request.getId());
        user.setDid(request.getDid());
        user.setPii(request.getPii());
        user.setData(request.getUserInfo());
    }
}
