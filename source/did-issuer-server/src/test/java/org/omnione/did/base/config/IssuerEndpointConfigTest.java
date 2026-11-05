package org.omnione.did.base.config;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.omnione.did.issuer.v1.agent.controller.oid4vc.CredentialIssuanceController;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class IssuerEndpointConfigTest {

    @Test
    void registersAllFixedRoutesWithoutIssuerMetadata() {
        RequestMappingHandlerMapping mapping = mock(RequestMappingHandlerMapping.class);
        CredentialIssuanceController controller = mock(CredentialIssuanceController.class);
        IssuerEndpointConfig config = new IssuerEndpointConfig(mapping, controller);

        config.registerIssuerEndpoints();

        ArgumentCaptor<RequestMappingInfo> routes = ArgumentCaptor.forClass(RequestMappingInfo.class);
        verify(mapping, times(6)).registerMapping(routes.capture(), eq(controller), any(Method.class));
        Map<String, RequestMethod> registered = routes.getAllValues().stream()
                .collect(Collectors.toMap(
                        route -> route.getPatternValues().iterator().next(),
                        route -> route.getMethodsCondition().getMethods().iterator().next()));
        assertThat(registered).containsExactlyInAnyOrderEntriesOf(Map.of(
                "/credential-offer/{request_id}/{configuration_id}", RequestMethod.GET,
                "/credential", RequestMethod.POST,
                "/nonce", RequestMethod.POST,
                "/credential-lifecycle/revoke", RequestMethod.POST,
                "/deferred_credential", RequestMethod.POST,
                "/notification", RequestMethod.POST));
    }
}
