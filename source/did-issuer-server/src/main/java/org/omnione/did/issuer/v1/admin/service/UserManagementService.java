package org.omnione.did.issuer.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.issuer.v1.admin.dto.user.CreateUserInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.user.UserDto;
import org.omnione.did.issuer.v1.admin.service.query.UserInfoQueryService;
import org.omnione.did.issuer.v1.admin.service.query.VcSchemaQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for managing user information in the Admin Console.
 * <p>
 * Provides operations to create, update, search, and retrieve users associated with Verifiable Credential schemas.
 */
@Transactional
@RequiredArgsConstructor
@Service
public class UserManagementService {

    private final UserInfoQueryService userQueryService;
    private final VcSchemaQueryService vcSchemaQueryService;

    /**
     * Searches for users based on a search key and value with pagination.
     *
     * @param searchKey   the field to search by (e.g., name or DID)
     * @param searchValue the value to match
     * @param pageable    pagination information
     * @return a page of UserDto
     */
    public Page<UserDto> searchUserInfoList(String searchKey, String searchValue, Pageable pageable) {
        return userQueryService.searchUserInfoList(searchKey, searchValue, pageable);
    }

    /**
     * Creates a new user with the provided information.
     *
     * @param request DTO containing user creation data
     */
    public void createUserInfo(CreateUserInfoReqDto request) {
        userQueryService.save(User.builder()
                .did(request.getDid())
                .pii(request.getFirstName() + request.getLastName()) // TODO: Trans PII
                .data(request.getUserInfo())
                .vcSchemaId(request.getVcSchemaId())
                .build());
    }

    /**
     * Finds a user by their ID and includes VC Schema ID for display.
     *
     * @param id the user ID
     * @return the user's data wrapped in a UserDto
     */
    public UserDto findById(Long id) {
        User user = userQueryService.findById(id);
        VcSchema vcSchema = vcSchemaQueryService.findById(user.getVcSchemaId());
        return UserDto.fromEntity(user, vcSchema.getVcSchemaId());
    }

    /**
     * Updates user information with the provided data.
     *
     * @param request DTO containing updated user info
     */
    public void updateUserInfo(CreateUserInfoReqDto request) {
        User user = userQueryService.findById(request.getId());
        user.setDid(request.getDid());
        user.setPii(request.getPii());
        user.setData(request.getUserInfo());
    }
}
