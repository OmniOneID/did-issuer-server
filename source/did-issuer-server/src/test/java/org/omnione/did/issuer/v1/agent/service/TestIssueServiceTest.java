package org.omnione.did.issuer.v1.agent.service;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.db.domain.User;
import org.omnione.did.base.db.repository.ZkpAttributeRepository;
import org.omnione.did.base.db.repository.ZkpSchemaAttributeRepository;
import org.omnione.did.issuer.v1.admin.service.query.IssueProfileQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpCredentialDefinitionQueryService;
import org.omnione.did.issuer.v1.admin.service.query.ZkpSchemaQueryService;
import org.omnione.did.issuer.v1.agent.service.provider.SpringIssueDataProvider;
import org.omnione.did.issuer.v1.agent.service.query.TransactionService;
import org.omnione.did.issuer.v1.agent.service.query.UserQueryService;
import org.omnione.did.issuer.v1.agent.service.query.VcSchemaService;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.sdk.issuer.model.data.Holder;
import org.omnione.did.sdk.issuer.model.domain.SdkUser;
import org.omnione.did.sdk.issuer.model.domain.SdkIssueProfile;
import org.omnione.did.sdk.issuer.model.domain.SdkVcProfile;
import org.omnione.did.sdk.issuer.protocol.vc.IssueVcProtocol;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestIssueServiceTest {

    @Test
    void piiMatchedUserReceivesHolderDidBeforeSdkProfileGeneration() {
        UserQueryService users = mock(UserQueryService.class);
        TestIssueService service = service(users);
        User registered = User.builder().id(7L).pii("holder-pii").vcSchemaId(3L).build();
        when(users.findByDidAndVcSchemaId("did:example:holder", 3L)).thenReturn(Optional.empty());
        when(users.findByPiiAndVcSchemaId("holder-pii", 3L)).thenReturn(Optional.of(registered));
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SdkUser resolved = service.findUserByHolderAndVcSchemaId(
                Holder.builder().pii("holder-pii").did("did:example:holder").build(), 3L);

        assertEquals(7L, resolved.getId());
        assertEquals("did:example:holder", resolved.getDid());
        assertEquals("did:example:holder", registered.getDid());
        verify(users).save(registered);

        when(users.findByIdAndVcSchemaId(7L, 3L)).thenReturn(registered);
        assertEquals("did:example:holder", service.findUserByVcProfileAndVcSchemaId(
                SdkVcProfile.builder().userId(7L).build(),
                SdkIssueProfile.builder().vcSchemaId(3L).build()).getDid());
    }

    @Test
    void existingDidIsRetainedWhenProxyHolderHasNoDid() {
        UserQueryService users = mock(UserQueryService.class);
        TestIssueService service = service(users);
        User registered = User.builder().id(7L).pii("holder-pii")
                .did("did:example:registered").vcSchemaId(3L).build();
        when(users.findByPiiAndVcSchemaId("holder-pii", 3L)).thenReturn(Optional.of(registered));
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SdkUser resolved = service.findUserByHolderAndVcSchemaId(
                Holder.builder().pii("holder-pii").build(), 3L);

        assertEquals("did:example:registered", resolved.getDid());
    }

    private TestIssueService service(UserQueryService users) {
        return new TestIssueService(mock(IssueVcProtocol.class), mock(SpringIssueDataProvider.class),
                mock(TransactionService.class), mock(StorageService.class), users,
                mock(VcSchemaService.class), mock(IssueProfileQueryService.class),
                mock(ZkpSchemaQueryService.class), mock(ZkpCredentialDefinitionQueryService.class),
                mock(ZkpSchemaAttributeRepository.class), mock(ZkpAttributeRepository.class));
    }
}
