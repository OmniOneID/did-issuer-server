package org.omnione.did.issuer.v1.agent.service;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.issuer.v1.agent.service.provider.SpringIssueDataProvider;
import org.omnione.did.issuer.v1.agent.service.query.TransactionService;
import org.omnione.did.issuer.v1.agent.service.query.UserQueryService;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.sdk.issuer.model.domain.SdkIssueProfile;
import org.omnione.did.sdk.issuer.model.data.Holder;
import org.omnione.did.sdk.issuer.model.domain.SdkVcProfile;
import org.omnione.did.sdk.issuer.model.domain.SdkUser;
import org.omnione.did.sdk.issuer.model.dto.UserInfoDto;
import org.omnione.did.sdk.issuer.protocol.vc.IssueVcProtocol;

import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IssueInitIssueServiceTest {
    @Test
    void holderDidIsUsedWhenRegisteredUserHasNoDid() {
        UserQueryService users = mock(UserQueryService.class);
        IssueInitIssueService service = new IssueInitIssueService(
                mock(IssueVcProtocol.class), mock(SpringIssueDataProvider.class),
                mock(TransactionService.class), users, mock(StorageService.class));
        when(users.findByPiiAndVcSchemaId("holder-pii", 3L)).thenReturn(
                Optional.of(User.builder().id(7L).pii("holder-pii").vcSchemaId(3L).build()));

        SdkUser resolved = service.findUserByHolderAndVcSchemaId(
                Holder.builder().pii("holder-pii").did("did:example:holder").build(), 3L);

        assertEquals(7L, resolved.getId());
        assertEquals("holder-pii", resolved.getPii());
        assertEquals("did:example:holder", resolved.getDid());
    }

    @Test
    void registeredDidIsRetainedWhenProxyHolderHasNoDid() {
        UserQueryService users = mock(UserQueryService.class);
        IssueInitIssueService service = new IssueInitIssueService(
                mock(IssueVcProtocol.class), mock(SpringIssueDataProvider.class),
                mock(TransactionService.class), users, mock(StorageService.class));
        when(users.findByPiiAndVcSchemaId("holder-pii", 3L)).thenReturn(
                Optional.of(User.builder().id(7L).pii("holder-pii")
                        .did("did:example:registered").vcSchemaId(3L).build()));

        SdkUser resolved = service.findUserByHolderAndVcSchemaId(
                Holder.builder().pii("holder-pii").build(), 3L);

        assertEquals("did:example:registered", resolved.getDid());
    }

    @Test
    void issuerInitiatedVcDatesFitLedgerMetadataColumns() {
        UserQueryService users = mock(UserQueryService.class);
        IssueInitIssueService service = new IssueInitIssueService(
                mock(IssueVcProtocol.class), mock(SpringIssueDataProvider.class),
                mock(TransactionService.class), users, mock(StorageService.class));
        long userId = 7L;
        long schemaId = 3L;
        when(users.findByIdAndVcSchemaId(userId, schemaId)).thenReturn(
                User.builder().id(userId).did("did:example:holder")
                        .vcSchemaId(schemaId).data("{}").build());

        UserInfoDto resolved = service.findUserByVcProfileAndVcSchemaId(
                SdkVcProfile.builder().userId(userId).build(),
                SdkIssueProfile.builder().vcSchemaId(schemaId).build());

        assertEquals(ZoneOffset.UTC, resolved.getValidFrom().getOffset());
        assertEquals(20, VerifiableCredential.dateToString(resolved.getValidFrom()).length());
        assertEquals(20, VerifiableCredential.dateToString(resolved.getValidUntil()).length());
    }
}
