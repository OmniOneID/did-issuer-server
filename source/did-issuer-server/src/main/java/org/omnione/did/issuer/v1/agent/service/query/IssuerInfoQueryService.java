package org.omnione.did.issuer.v1.agent.service.query;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.IssuerInfo;
import org.omnione.did.base.db.repository.IssuerInfoRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.stereotype.Service;

/**
 * Description...
 */
@RequiredArgsConstructor
@Service
public class IssuerInfoQueryService {

    private final IssuerInfoRepository issuerInfoRepository;

    private static IssuerInfo issuerInfo;

    @PostConstruct
    public void loadData() {
        issuerInfo = issuerInfoRepository.findFirstBy().orElseThrow(()
                -> new OpenDidException(ErrorCode.TODO));
    }
    public IssuerInfo getIssuerInfo() {
        if (issuerInfo == null) {
            loadData();
        }
        return issuerInfo;
    }

}
