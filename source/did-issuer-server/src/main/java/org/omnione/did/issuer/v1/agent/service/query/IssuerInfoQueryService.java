package org.omnione.did.issuer.v1.agent.service.query;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.IssuerInfo;
import org.omnione.did.base.db.repository.IssuerInfoRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Description...
 */
@Profile("!sample")
@Service
public class IssuerInfoQueryService {

    private final IssuerInfoRepository issuerInfoRepository;

    @Getter
    private final IssuerInfo issuerInfo;

    public IssuerInfoQueryService(IssuerInfoRepository issuerInfoRepository) {
        this.issuerInfoRepository = issuerInfoRepository;
        this.issuerInfo = issuerInfoRepository.findFirstBy().orElseThrow(()
                -> new OpenDidException(ErrorCode.ISSUER_INFO_NOT_FOUND));;
    }

    public IssuerInfo getIssuerInfoOrNull() {
        return issuerInfoRepository.findFirstBy().orElse(null);
    }

    public void save(IssuerInfo issuerInfo) {
        issuerInfoRepository.save(issuerInfo);

        this.issuerInfo.setName(issuerInfo.getName());
        this.issuerInfo.setStatus(issuerInfo.getStatus());
        this.issuerInfo.setServerUrl(issuerInfo.getServerUrl());
        this.issuerInfo.setCertificateUrl(issuerInfo.getCertificateUrl());
    }

}
