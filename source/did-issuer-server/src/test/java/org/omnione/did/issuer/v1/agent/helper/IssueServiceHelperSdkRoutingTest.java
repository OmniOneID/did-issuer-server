package org.omnione.did.issuer.v1.agent.helper;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.datamodel.enums.InitiateType;
import org.omnione.did.base.datamodel.enums.UserQueryType;
import org.omnione.did.base.db.domain.IssueProfile;
import org.omnione.did.base.db.domain.Transaction;
import org.omnione.did.issuer.v1.admin.service.query.IssueProfileQueryService;
import org.omnione.did.issuer.v1.agent.service.IssueInitIssueService;
import org.omnione.did.issuer.v1.agent.service.TestIssueService;
import org.omnione.did.issuer.v1.agent.service.UserInitIssueService;
import org.omnione.did.issuer.v1.agent.service.UserQueryApiIssueService;
import org.omnione.did.issuer.v1.agent.service.query.TransactionService;
import org.omnione.did.sdk.issuer.model.dto.GenerateIssueProfileReqDto;
import org.omnione.did.sdk.issuer.model.dto.GenerateIssueProfileResDto;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class IssueServiceHelperSdkRoutingTest {
    @Test
    void routesTestProfilesToSdkBackedTestService() {
        UserInitIssueService userInit = mock(UserInitIssueService.class);
        IssueInitIssueService issueInit = mock(IssueInitIssueService.class);
        IssueProfileQueryService profiles = mock(IssueProfileQueryService.class);
        TransactionService transactions = mock(TransactionService.class);
        UserQueryApiIssueService api = mock(UserQueryApiIssueService.class);
        TestIssueService test = mock(TestIssueService.class);
        IssueServiceHelper helper = new IssueServiceHelper(userInit, issueInit, profiles, transactions, api, test);

        Transaction transaction = mock(Transaction.class);
        IssueProfile profile = mock(IssueProfile.class);
        GenerateIssueProfileReqDto request = new GenerateIssueProfileReqDto();
        request.setTxId("tx-1");
        GenerateIssueProfileResDto expected = mock(GenerateIssueProfileResDto.class);
        when(transactions.findByTxId("tx-1")).thenReturn(transaction);
        when(transaction.getIssueProfileId()).thenReturn(12L);
        when(profiles.findById(12L)).thenReturn(profile);
        when(profile.getUserQueryType()).thenReturn(UserQueryType.TEST);
        when(profile.getInitiateType()).thenReturn(InitiateType.ISSUER_INIT);
        when(test.generateIssueProfile(request)).thenReturn(expected);

        assertSame(expected, helper.generateIssueProfile(request));
        verifyNoInteractions(userInit, api);
        verify(issueInit, never()).generateIssueProfile(request);
    }
}
