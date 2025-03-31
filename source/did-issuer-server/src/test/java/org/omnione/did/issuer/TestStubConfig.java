package org.omnione.did.issuer;

import org.omnione.did.issuer.v1.admin.service.IssueProfileService;
import org.omnione.did.issuer.v1.admin.service.ListCommunityService;
import org.omnione.did.issuer.v1.admin.service.VcSchemaManagerService;
import org.omnione.did.issuer.v1.agent.service.EnrollEntityServiceImpl;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.omnione.did.issuer.v1.agent.service.query.VcSchemaService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

/**
 * Description...
 */
@TestConfiguration
public class TestStubConfig {
    @MockBean
    private EnrollEntityServiceImpl enrollEntityService;
    @MockBean
    private VcSchemaManagerService vcSchemaManagerService;
    @MockBean
    private IssueProfileService issueProfileService;
    @MockBean
    private ListCommunityService listCommunityService;
    @MockBean
    private IssuerInfoQueryService issuerInfoQueryService;
    @MockBean
    private VcSchemaService vcSchemaService;
}

