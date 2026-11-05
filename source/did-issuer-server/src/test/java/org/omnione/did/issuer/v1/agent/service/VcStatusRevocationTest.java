package org.omnione.did.issuer.v1.agent.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.issuer.v1.agent.service.provider.SpringStorageProvider;
import org.omnione.did.issuer.v1.agent.service.provider.SpringVcStatusDataProvider;
import org.omnione.did.issuer.v1.agent.service.query.TransactionService;
import org.omnione.did.sdk.issuer.exception.IssuerSdkException;
import org.omnione.did.sdk.issuer.model.common.enums.SubTransactionType;
import org.omnione.did.sdk.issuer.model.data.ReqRevokeVc;
import org.omnione.did.sdk.issuer.model.domain.SdkTransaction;
import org.omnione.did.sdk.issuer.model.dto.RevokeVcReqDto;
import org.omnione.did.sdk.issuer.model.dto.RevokeVcResDto;
import org.omnione.did.sdk.issuer.protocol.vc.VcStatusProtocol;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VcStatusRevocationTest {

    @Mock private VcStatusProtocol protocol;
    @Mock private SpringVcStatusDataProvider dataProvider;
    @Mock private TransactionService transactionService;
    @Mock private SpringStorageProvider storageProvider;

    private VcStatusServiceImpl service;
    private RevokeVcReqDto request;
    private SdkTransaction transaction;

    @BeforeEach
    void setUp() {
        service = new VcStatusServiceImpl(protocol, dataProvider, transactionService, storageProvider);
        request = RevokeVcReqDto.builder()
                .txId("revoke-tx")
                .request(ReqRevokeVc.builder().vcId("vc-123").build())
                .build();
        transaction = SdkTransaction.builder().id(11L).build();
    }

    @Test
    void publishesRevokedStatusBeforeRecordingSuccessfulRevokeStep() {
        when(dataProvider.validateAndFindTransaction("revoke-tx", SubTransactionType.INSPECT_REVOKE_PROPOSE))
                .thenReturn(transaction);
        when(protocol.revokeVc(request, transaction))
                .thenReturn(RevokeVcResDto.builder().txId("revoke-tx").build());

        service.revokeVc(request);

        InOrder order = inOrder(protocol, storageProvider, transactionService);
        order.verify(protocol).revokeVc(request, transaction);
        order.verify(storageProvider).updateVcStatus("vc-123", VcStatus.REVOKED);
        order.verify(transactionService).insertSubTransaction(any());
    }

    @Test
    void doesNotReportSuccessfulRevokeWhenMetadataUpdateFails() {
        when(dataProvider.validateAndFindTransaction("revoke-tx", SubTransactionType.INSPECT_REVOKE_PROPOSE))
                .thenReturn(transaction);
        when(protocol.revokeVc(request, transaction))
                .thenReturn(RevokeVcResDto.builder().txId("revoke-tx").build());
        doThrow(new IllegalStateException("metadata unavailable"))
                .when(storageProvider).updateVcStatus("vc-123", VcStatus.REVOKED);

        assertThatThrownBy(() -> service.revokeVc(request)).isInstanceOf(IssuerSdkException.class);
        verify(transactionService, never()).insertSubTransaction(any());
    }
}
