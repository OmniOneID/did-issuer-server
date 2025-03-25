package org.omnione.did.issuer.v1.admin.service.query;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.db.repository.UserRepository;
import org.omnione.did.base.db.repository.VcSchemaRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.dto.IssueProfileDto;
import org.omnione.did.issuer.v1.admin.dto.NamespaceDto;
import org.omnione.did.issuer.v1.admin.dto.UserDto;
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
public class UserInfoQueryService {
    private final UserRepository userRepository;
    private final VcSchemaRepository vcSchemaRepository;


    public Page<UserDto> searchUserInfoList(String searchKey, String searchValue, Pageable pageable) {
        Page<User> entityPage = userRepository.searchUser(searchKey, searchValue, pageable);
        List<UserDto> namespaceDtos = entityPage.getContent().stream()
                .map(user -> {
                    VcSchema vcSchema = vcSchemaRepository.findById(user.getVcSchemaId())
                            .orElseThrow(() -> new OpenDidException(ErrorCode.VC_SCHEMA_NOT_FOUND));
                    return UserDto.fromEntity(user, vcSchema.getVcSchemaId());
                })
                .collect(Collectors.toList());

        return new PageImpl<>(namespaceDtos, pageable, entityPage.getTotalElements());
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new OpenDidException(ErrorCode.USER_NOT_FOUND));
    }
}
