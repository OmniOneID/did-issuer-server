package org.omnione.did.issuer.v1.agent.service;

import org.omnione.did.common.util.DateTimeUtil;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.sdk.issuer.protocol.vc.IssueVcProtocol;
import org.omnione.did.sdk.issuer.model.dto.UserInfoDto;
import org.omnione.did.issuer.v1.agent.service.provider.SpringIssueDataProvider;
import org.omnione.did.sdk.issuer.model.data.Holder;
import org.omnione.did.base.db.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.sdk.issuer.model.domain.SdkIssueProfile;
import org.omnione.did.sdk.issuer.model.domain.SdkUser;
import org.omnione.did.sdk.issuer.model.domain.SdkVcProfile;

import org.omnione.did.issuer.v1.agent.service.query.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This service provides methods for issuing a Verifiable Credential (VC) for a
 * User initiate.
 */
import org.omnione.did.issuer.v1.agent.service.query.TransactionService;

@Slf4j
@Service
@Transactional
@Profile("!sample")
public class UserInitIssueService extends AbstractPersistentIssueVcService {
    private final UserQueryService userQueryService;

    public UserInitIssueService(IssueVcProtocol issueVcProtocol,
                                SpringIssueDataProvider issuerDataProvider,
                                TransactionService transactionService,
                                UserQueryService userQueryService,
                                StorageService storageService) {
        super(issueVcProtocol, issuerDataProvider, transactionService, storageService);
        this.userQueryService = userQueryService;
    }

    @Override
    protected UserInfoDto findUserByVcProfileAndVcSchemaId(SdkVcProfile vcProfile, SdkIssueProfile issueProfile) {
        User user = userQueryService.findByDidAndVcSchemaId(vcProfile.getDid(), issueProfile.getVcSchemaId())
                .orElseThrow(() -> new OpenDidException(ErrorCode.HOLDER_NOT_FOUND));

        return UserInfoDto.builder()
                .id(user.getId())
                .pii(user.getPii())
                .did(user.getDid())
                .vcSchemaId(user.getVcSchemaId())
                .data(user.getData())
                .validFrom(DateTimeUtil.getCurrentUTCTime())
                .validUntil(DateTimeUtil.addYearsToCurrentTime(2))
                .build();
    }

    @Override
    protected SdkUser findUserByHolderAndVcSchemaId(Holder holder, Long vcSchemaId) {
        // The SDK stores user.did in the VC profile. Preserve the holder DID
        // even though this step does not resolve a persisted User yet.
        return toSdkUser(User.builder().did(holder.getDid()).build());
    }

    private SdkUser toSdkUser(User user) {
        if (user == null) {
            return null;
        }
        return SdkUser.builder()
                .id(user.getId())
                .pii(user.getPii())
                .did(user.getDid())
                .vcSchemaId(user.getVcSchemaId())
                .data(user.getData())
                .build();
    }
}
