package org.omnione.did.issuer.v1.common.service;

import org.omnione.did.base.constants.ConfigType;
import org.omnione.did.base.db.domain.ServerConfig;
import org.omnione.did.base.db.repository.ServerConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ServerConfigService {
    private final ServerConfigRepository serverConfigRepository;

    public Long findTransactionExpirationTime() {
        return serverConfigRepository.findByConfigKey(ConfigType.TRANSACTION_EXPIRATION_TIME.name())
                .map(ServerConfig::getConfigValue)
                .map(this::safeParseLong)
                .orElse(3600L);
    }

    private Long safeParseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 3600L;
        }
    }
}
