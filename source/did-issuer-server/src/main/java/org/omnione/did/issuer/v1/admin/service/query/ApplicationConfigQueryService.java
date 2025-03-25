package org.omnione.did.issuer.v1.admin.service.query;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.db.domain.ApplicationConfig;
import org.omnione.did.base.db.repository.ApplicationConfigRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.stereotype.Service;

/**
 * Description...
 */
@RequiredArgsConstructor
@Service
public class ApplicationConfigQueryService {
    private final ApplicationConfigRepository applicationConfigRepository;

    public ApplicationConfig getApplicationConfig() {
        return applicationConfigRepository.findFirstBy().orElseThrow(()
                -> new OpenDidException(ErrorCode.APPLICATION_CONFIG_NOT_FOUND));
    }
}
