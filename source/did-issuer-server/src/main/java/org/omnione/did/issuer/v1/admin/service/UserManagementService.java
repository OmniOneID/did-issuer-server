package org.omnione.did.issuer.v1.admin.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseDigestUtil;
import org.omnione.did.common.util.HexUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.issuer.v1.admin.dto.user.CreateUserInfoFromDemoReqDto;
import org.omnione.did.issuer.v1.admin.dto.user.CreateUserInfoReqDto;
import org.omnione.did.issuer.v1.admin.dto.user.SerializeUserInfoData;
import org.omnione.did.issuer.v1.admin.dto.user.UserDto;
import org.omnione.did.issuer.v1.admin.service.query.UserInfoQueryService;
import org.omnione.did.issuer.v1.admin.service.query.VcSchemaQueryService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

/**
 * Service for managing user information in the Admin Console.
 * <p>
 * Provides operations to create, update, search, and retrieve users associated with Verifiable Credential schemas.
 */
@RequiredArgsConstructor
@Transactional
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
    public PageImpl<UserDto> searchUserInfoList(String searchKey, String searchValue, Pageable pageable) {
        return userQueryService.searchUserInfoList(searchKey, searchValue, pageable);
    }

    /**
     * Creates a new user with the provided information.
     *
     * @param request DTO containing user creation data
     */
    public void createUserInfo(CreateUserInfoReqDto request) {
        String pii = JsonUtil.serializeAndSort(SerializeUserInfoData.builder()
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .build());
        byte[] hashedDataBytes = BaseDigestUtil.generateHash(pii.getBytes(StandardCharsets.UTF_8));
        String hexStringPii = HexUtil.toHexString(hashedDataBytes);

        userQueryService.save(User.builder()
                .did(request.getDid())
                .pii(hexStringPii) // TODO: Trans PII
                .data(request.getUserInfo())
                .vcSchemaId(request.getVcSchemaId())
                .build());
    }

    public void createUserInfo(CreateUserInfoFromDemoReqDto request) {
        if (request.getDid() == null && request.getPii() == null) {
            throw new OpenDidException(ErrorCode.HOLDER_INVALID);
        }

        String vcSchemaInput = request.getVcSchemaId();
        String vcSchemaName = extractNameOrUseAsIs(vcSchemaInput);

        Long vcSchemaId = vcSchemaQueryService.findByVcSchemaId(vcSchemaName).getId();
        User existedUser;

        if (request.getDid() != null && !request.getDid().isEmpty()) {
            existedUser = userQueryService.findByDidAndVcSchemaIdOrNew(request.getDid(), vcSchemaId);
        } else {
            existedUser = userQueryService.findByPiiAndVcSchemaIdOrNew(request.getPii(), vcSchemaId);
        }

        userQueryService.save(User.builder()
                .id(existedUser.getId())
                .did(request.getDid())
                .pii(request.getPii())
                .data(request.getUserInfo())
                .vcSchemaId(vcSchemaId)
                .build());
    }

    private String extractNameOrUseAsIs(String input) {
        try {
            URI uri = new URI(input);
            String query = uri.getQuery();
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2 && "name".equals(keyValue[0])) {
                        return keyValue[1];
                    }
                }
            }
        } catch (URISyntaxException e) {
            throw new OpenDidException(ErrorCode.VC_SCHEMA_NAME_INVALID);
        }
        return input;
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

