package org.omnione.did.issuer.v1.admin.service;

import com.google.gson.Gson;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.constants.ConfigType;
import org.omnione.did.base.db.domain.ServerConfig;
import org.omnione.did.base.db.repository.ServerConfigRepository;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.issuer.v1.admin.api.Oid4vciListProviderFeign;
import org.omnione.did.issuer.v1.admin.dto.oid4vci.ListProviderRegistrationDto;
import org.omnione.did.issuer.v1.admin.dto.oid4vci.ListProviderRegistrationReqDto;
import org.omnione.did.issuer.v1.admin.dto.oid4vci.ListProviderRegistrationResultDto;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class Oid4vciListProviderRegistrationService {
    private final Oid4vciListProviderFeign listProviderFeign;
    private final IssuerInfoQueryService issuerInfoQueryService;
    private final ServerConfigRepository serverConfigRepository;
    private final Gson gson;

    @Value("${list.url:http://127.0.0.1:8090}")
    private String listProviderUrl;

    @Value("${issuer.base-url:http://127.0.0.1:8091}")
    private String issuerBaseUrl;

    public ListProviderRegistrationDto getRegistration() {
        return serverConfigRepository.findByConfigKey(configKey())
                .map(config -> gson.fromJson(config.getConfigValue(), ListProviderRegistrationDto.class))
                .orElseGet(this::defaultRegistration);
    }

    @Transactional
    public ListProviderRegistrationDto register(ListProviderRegistrationReqDto request) {
        try {
            ListProviderRegistrationResultDto result = listProviderFeign.registerIssuer(request);
            ListProviderRegistrationDto registration = ListProviderRegistrationDto.builder()
                    .registrationId(result.getId())
                    .status(result.getStatus())
                    .listProviderUrl(listProviderUrl)
                    .credentialIssuer(request.getCredentialIssuer())
                    .credentialIssuerMetadataUri(request.getCredentialIssuerMetadataUri())
                    .userInitiationUri(request.getUserInitiationUri())
                    .requestedAt(Instant.now())
                    .build();
            save(registration);
            return registration;
        } catch (FeignException.Conflict e) {
            throw new OpenDidException(ErrorCode.OID4VCI_LIST_ISSUER_ALREADY_REGISTERED);
        } catch (FeignException e) {
            throw new OpenDidException(ErrorCode.OID4VCI_LIST_REGISTRATION_FAILED);
        }
    }

    private ListProviderRegistrationDto defaultRegistration() {
        String baseUrl = trimTrailingSlash(issuerBaseUrl);
        return ListProviderRegistrationDto.builder()
                .listProviderUrl(listProviderUrl)
                .credentialIssuer(issuerInfoQueryService.getIssuerInfo().getDid())
                .credentialIssuerMetadataUri(baseUrl + "/.well-known/openid-credential-issuer")
                .userInitiationUri(baseUrl + "/oid4vci/issuance/start")
                .build();
    }

    private void save(ListProviderRegistrationDto registration) {
        ServerConfig config = serverConfigRepository.findByConfigKey(configKey())
                .orElse(ServerConfig.builder()
                        .configKey(configKey())
                        .description("OID4VCI List Provider Registration")
                        .build());
        config.setConfigValue(gson.toJson(registration));
        serverConfigRepository.save(config);
    }

    private String configKey() {
        return ConfigType.OID4VCI_LIST_PROVIDER_REGISTRATION.name();
    }

    private String trimTrailingSlash(String value) {
        return value != null && value.endsWith("/")
                ? value.substring(0, value.length() - 1)
                : value;
    }
}
