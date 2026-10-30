package org.omnione.did.issuer.v1.agent.service;

import org.omnione.did.sdk.issuer.model.dto.*;

public interface IssueService {
    OfferIssueVcResDto requestOffer(OfferIssueVcReqDto request);

    InspectIssueProposeResDto inspectIssuePropose(InspectIssueProposeReqDto request);

    GenerateIssueProfileResDto generateIssueProfile(GenerateIssueProfileReqDto request);

    IssueVcResDto issueVc(IssueVcReqDto request);

    GenerateIssueProfileResDto generateIssueProfileFromProxy(GenerateIssueProfileReqDto request, String accessToken);

    IssueVcResDto issueVcFromProxy(IssueVcReqDto request);

    CompleteVcResDto completeVc(CompleteVcReqDto request);

    IssueVcResultResDto issueVcResult(String offerId);
}
