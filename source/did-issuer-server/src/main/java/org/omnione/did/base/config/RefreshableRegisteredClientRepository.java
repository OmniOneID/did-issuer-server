package org.omnione.did.base.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.datamodel.data.oid4vci.Oid4vcProperty;
import org.omnione.did.issuer.v1.admin.service.Oid4vciConfigService;
import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class RefreshableRegisteredClientRepository implements RegisteredClientRepository {
    private final Oid4vciConfigService configService;
    private final Function<Oid4vcProperty, List<RegisteredClient>> clientFactory;
    private volatile InMemoryRegisteredClientRepository delegate;

    public void refresh(Oid4vcProperty properties) {
        delegate = new InMemoryRegisteredClientRepository(clientFactory.apply(properties));
        log.info("OID4VCI registered client configuration refreshed");
    }

    @EventListener
    public void onPropertiesUpdated(Oid4vciConfigService.PropertiesUpdatedEvent event) throws IOException {
        refresh(configService.getProperties());
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        delegate.save(registeredClient);
    }

    @Override
    public RegisteredClient findById(String id) {
        return delegate.findById(id);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return delegate.findByClientId(clientId);
    }
}
