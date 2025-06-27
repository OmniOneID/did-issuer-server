package org.omnione.did.base.config;

import org.mockito.Mockito;
import org.omnione.did.issuer.v1.admin.service.IssueProfileService;
import org.omnione.did.issuer.v1.admin.service.ListCommunityService;
import org.omnione.did.issuer.v1.admin.service.VcSchemaManagerService;
import org.omnione.did.issuer.v1.agent.service.EnrollEntityServiceImpl;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.omnione.did.issuer.v1.agent.service.query.VcSchemaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Description...
 */
@Configuration
@Profile("sample")
public class StubConfig {

        @Bean
        public VcSchemaManagerService vcSchemaManagerService() {
            return Mockito.mock(VcSchemaManagerService.class);

        }

        @Bean
        public EnrollEntityServiceImpl enrollEntityService() {

            return Mockito.mock(EnrollEntityServiceImpl.class);
        }

        @Bean
        public IssueProfileService issueProfileService() {
            return Mockito.mock(IssueProfileService.class);

        }

        @Bean
        public ListCommunityService listCommunityService() {
            return Mockito.mock(ListCommunityService.class);

        }

        @Bean
        public IssuerInfoQueryService issuerInfoQueryService() {
            return Mockito.mock(IssuerInfoQueryService.class);

        }

        @Bean
        public VcSchemaService vcSchemaService() {
            return Mockito.mock(VcSchemaService.class);

        }

}
