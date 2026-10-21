package org.omnione.did.issuer.v1.agent.service.oid4vc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.omnione.did.base.db.domain.IssuanceState;
import org.omnione.did.base.db.domain.Oid4vcCredentialIssuanceEntity;
import org.omnione.did.base.db.repository.Oid4vcCredentialIssuanceRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.CredentialManagementService;
import org.omnione.did.issuer.v1.agent.service.oid4vc.status.model.CredentialIssuanceView;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.oid4vc.oid4vci.dto.revocation.CredentialRevocationRequest;
import org.omnione.did.oid4vc.oid4vci.dto.revocation.CredentialRevocationResponse;
import org.omnione.did.oid4vc.oid4vci.revocation.model.VerifiedCredentialRevocation;
import org.omnione.did.oid4vc.oid4vci.revocation.service.CredentialRevocationTokenVerifier;
import org.omnione.did.oid4vc.oid4vci.service.store.CNonceStore;
import org.omnione.did.oid4vc.oid4vci.status.model.CredentialStatus;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CredentialRevocationServiceTest {

    private static final String HOLDER_DID = "did:omn:holder";
    private static final String STATUS_URI = "https://issuer.example/status-lists/1";
    private static final Instant REVOKED_AT = Instant.parse("2026-10-14T01:02:03Z");

    @Mock
    private CredentialRevocationTokenVerifier tokenVerifier;
    @Mock
    private Oid4vcCredentialIssuanceRepository issuanceRepository;
    @Mock
    private CredentialManagementService credentialManagementService;
    @Mock
    private CNonceStore cNonceStore;
    @Mock
    private StorageService storageService;

    private CredentialRevocationService service;
    private CredentialRevocationRequest request;
    private Oid4vcCredentialIssuanceEntity issuance;

    @BeforeEach
    void setUp() throws Exception {
        service = new CredentialRevocationService(
                tokenVerifier, issuanceRepository, credentialManagementService,
                cNonceStore, storageService);
        request = new CredentialRevocationRequest();
        request.setRevocationToken("signed-token");
        when(tokenVerifier.verify(eq("signed-token"), any())).thenReturn(
                new VerifiedCredentialRevocation(
                        HOLDER_DID, HOLDER_DID + "#auth", "nonce-1",
                        STATUS_URI, 42, REVOKED_AT.minusSeconds(30),
                        REVOKED_AT.plusSeconds(270)));

        issuance = new Oid4vcCredentialIssuanceEntity();
        issuance.setIssuanceId("issuance-1");
        issuance.setUserId("user-1");
        issuance.setSubjectDid(HOLDER_DID);
        issuance.setIssuanceState(IssuanceState.ISSUED);
        issuance.setCredentialStatus(CredentialStatus.VALID);
        when(issuanceRepository.findByStatusReferenceForUpdate(STATUS_URI, 42))
                .thenReturn(Optional.of(issuance));
    }

    @Test
    void consumesNonceAndRevokesOwnedCredential() throws Exception {
        when(cNonceStore.consume("nonce-1")).thenReturn(true);
        when(credentialManagementService.changeStatus(
                "issuance-1", CredentialStatus.INVALID, "USER_REQUEST", HOLDER_DID))
                .thenReturn(view(CredentialStatus.INVALID, REVOKED_AT));

        CredentialRevocationResponse response = service.revoke(request);

        assertThat(response.credentialStatus().status()).isEqualTo("INVALID");
        assertThat(response.credentialStatus().statusList().uri()).isEqualTo(STATUS_URI);
        assertThat(response.credentialStatus().statusList().idx()).isEqualTo(42);
        assertThat(response.revokedAt()).isEqualTo(REVOKED_AT);
    }

    @Test
    void rejectsConsumedNonceBeforeStateChange() {
        when(cNonceStore.consume("nonce-1")).thenReturn(false);

        assertThatThrownBy(() -> service.revoke(request))
                .isInstanceOfSatisfying(OpenDidException.class, exception ->
                        assertThat(exception.getErrorCode())
                                .isEqualTo(ErrorCode.ISSUER_NONCE_INVALID));
        try {
            verify(credentialManagementService, never()).changeStatus(any(), any(), any(), any());
        } catch (Exception exception) {
            throw new AssertionError(exception);
        }
    }

    @Test
    void returnsSuccessWithoutDuplicateHistoryWhenAlreadyInvalid() throws Exception {
        issuance.setCredentialStatus(CredentialStatus.INVALID);
        issuance.setStatusChangedAt(REVOKED_AT);
        when(cNonceStore.consume("nonce-1")).thenReturn(true);

        CredentialRevocationResponse response = service.revoke(request);

        assertThat(response.revokedAt()).isEqualTo(REVOKED_AT);
        verify(credentialManagementService, never()).changeStatus(any(), any(), any(), any());
    }

    private CredentialIssuanceView view(CredentialStatus status, Instant statusChangedAt) {
        return new CredentialIssuanceView(
                "issuance-1", "user-1", "config-1", "dc+sd-jwt-did",
                STATUS_URI, 42, status, IssuanceState.ISSUED,
                REVOKED_AT.minusSeconds(3600), null, statusChangedAt,
                null, null, null, REVOKED_AT.minusSeconds(3600), statusChangedAt);
    }
}
