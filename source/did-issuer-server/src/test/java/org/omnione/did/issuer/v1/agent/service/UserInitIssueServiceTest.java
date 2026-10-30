package org.omnione.did.issuer.v1.agent.service;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.issuer.v1.agent.service.provider.SpringIssueDataProvider;
import org.omnione.did.issuer.v1.agent.service.query.TransactionService;
import org.omnione.did.issuer.v1.agent.service.query.UserQueryService;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.sdk.issuer.model.data.Holder;
import org.omnione.did.sdk.issuer.model.domain.SdkIssueProfile;
import org.omnione.did.sdk.issuer.model.domain.SdkUser;
import org.omnione.did.sdk.issuer.model.domain.SdkVcProfile;
import org.omnione.did.sdk.issuer.model.dto.UserInfoDto;
import org.omnione.did.sdk.issuer.protocol.vc.IssueVcProtocol;

import java.util.Optional;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserInitIssueServiceTest {
    @Test
    void holderDidSurvivesProfileGenerationAndResolvesUserAtIssuance() {
        String holderDid = "did:example:holder";
        long vcSchemaId = 3L;
        UserQueryService users = mock(UserQueryService.class);
        UserInitIssueService service = new UserInitIssueService(
                mock(IssueVcProtocol.class), mock(SpringIssueDataProvider.class),
                mock(TransactionService.class), users, mock(StorageService.class));

        SdkUser profileUser = service.findUserByHolderAndVcSchemaId(
                Holder.builder().did(holderDid).build(), vcSchemaId);
        SdkVcProfile savedProfile = SdkVcProfile.builder().did(profileUser.getDid()).build();
        SdkIssueProfile issueProfile = SdkIssueProfile.builder().vcSchemaId(vcSchemaId).build();
        when(users.findByDidAndVcSchemaId(holderDid, vcSchemaId))
                .thenReturn(Optional.of(User.builder().id(7L).did(holderDid)
                        .vcSchemaId(vcSchemaId).data("{}").build()));

        UserInfoDto resolved = service.findUserByVcProfileAndVcSchemaId(savedProfile, issueProfile);

        assertEquals(holderDid, savedProfile.getDid());
        assertEquals(7L, resolved.getId());
        assertEquals(ZoneOffset.UTC, resolved.getValidFrom().getOffset());
        assertEquals(20, VerifiableCredential.dateToString(resolved.getValidFrom()).length());
        verify(users).findByDidAndVcSchemaId(holderDid, vcSchemaId);
    }
}
