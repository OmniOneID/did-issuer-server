package org.omnione.did.base.db.repository;

import org.junit.jupiter.api.Test;
import org.omnione.did.InMemoryDataJpaTest;
import org.omnione.did.base.config.JpaConfig;
import org.omnione.did.base.config.QuerydslConfig;
import org.omnione.did.base.constants.ApiType;
import org.omnione.did.base.db.constant.AdminRole;
import org.omnione.did.base.db.domain.*;
import org.omnione.did.sdk.issuer.model.common.enums.ZkpCredentialDefinitionStatus;
import org.omnione.did.sdk.issuer.model.common.enums.ZkpSchemaStatus;
import org.omnione.did.zkp.datamodel.enums.CredentialType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.assertj.core.api.Assertions.assertThat;

@InMemoryDataJpaTest
class AdminSearchPartialMatchTest {
    private static final PageRequest FIRST_PAGE = PageRequest.of(0, 10);

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = Namespace.class)
    @EnableJpaRepositories(basePackageClasses = NamespaceRepository.class)
    @Import({JpaConfig.class, QuerydslConfig.class})
    static class TestConfiguration {
    }

    @Autowired private NamespaceRepository namespaces;
    @Autowired private ZkpNamespaceRepository zkpNamespaces;
    @Autowired private ZkpSchemaRepository zkpSchemas;
    @Autowired private ZkpCredentialDefinitionRepository definitions;
    @Autowired private VcRepository vcs;
    @Autowired private VcSchemaRepository vcSchemas;
    @Autowired private UserRepository users;
    @Autowired private AdminRepository admins;
    @Autowired private ApiLogRepository logs;

    @Test
    void namespaceAndZkpSearchesMatchSubstrings() {
        namespaces.save(Namespace.builder().namespaceId("org.example.identity").name("Identity namespace").build());
        zkpNamespaces.save(ZkpNamespace.builder().namespaceId("org.example.zkp").name("ZKP identity").build());
        zkpSchemas.save(ZkpSchema.builder().schemaId("schema.identity.v1")
                .name("Identity schema").version("1").tag("identity").status(ZkpSchemaStatus.ACTIVATE)
                .schema("{}").build());
        definitions.save(ZkpCredentialDefinition.builder().definitionId("definition.identity.v1")
                .schemaId("schema.identity.v1").type(CredentialType.values()[0]).alias("identity")
                .tag("identity").version("1").definition("{}").zkpSchemaId(1L)
                .status(ZkpCredentialDefinitionStatus.ACTIVATE).build());

        assertThat(namespaces.searchNamespaces("namespaceId", "example", FIRST_PAGE).getTotalElements()).isEqualTo(1);
        assertThat(namespaces.searchNamespaces("name", "entity", FIRST_PAGE).getTotalElements()).isEqualTo(1);
        assertThat(zkpNamespaces.searchNamespaces("name", "identity", FIRST_PAGE).getTotalElements()).isEqualTo(1);
        assertThat(zkpSchemas.searchSchemas("schemaId", "identity", FIRST_PAGE).getTotalElements()).isEqualTo(1);
        assertThat(zkpSchemas.searchSchemas("name", "tity sch", FIRST_PAGE).getTotalElements()).isEqualTo(1);
        assertThat(definitions.searchCredentialDefinitions("definitionId", "identity", FIRST_PAGE).getTotalElements()).isEqualTo(1);
    }

    @Test
    void issuedVcUserAndAdminSearchesMatchSubstrings() {
        vcs.save(Vc.builder().vcId("vc-identity-123").did("did:example:holder123").vcType("VC").build());
        VcSchema matchingSchema = vcSchemas.save(VcSchema.builder().vcSchemaId("schema.identity.v1").build());
        VcSchema otherSchema = vcSchemas.save(VcSchema.builder().vcSchemaId("schema.address.v1").build());
        users.save(User.builder().did("did:example:holder123").vcSchemaId(matchingSchema.getId()).build());
        users.save(User.builder().did("did:example:other456").vcSchemaId(otherSchema.getId()).build());
        admins.save(Admin.builder().loginId("admin@example.com").loginPassword("hash")
                .passwordAlgo("bcrypt").emailVerified(true).requirePasswordReset(false)
                .role(AdminRole.ROOT).createdBy("system").build());

        assertThat(vcs.searchIssuedVc("vcId", "identity", FIRST_PAGE).getTotalElements()).isEqualTo(1);
        assertThat(vcs.searchIssuedVc("did", "holder", FIRST_PAGE).getTotalElements()).isEqualTo(1);
        assertThat(users.searchUser("did", "holder", FIRST_PAGE).getTotalElements()).isEqualTo(1);
        assertThat(users.searchUser("vcSchemaId", "identity", FIRST_PAGE).getTotalElements()).isEqualTo(1);
        assertThat(admins.searchAdmins("loginId", "example", FIRST_PAGE).getTotalElements()).isEqualTo(1);
    }

    @Test
    void auditLogSearchesMatchTypedSubstrings() {
        logs.save(ApiLog.builder().method("GET").uri("/issuer/admin/v1/test")
                .status(200).result("SUCCESS").apiType(ApiType.ADMIN)
                .targetType("OID4VCI_CREDENTIAL").requesterId("admin@example.com").build());

        assertThat(logs.searchAuditApiLogs("targetType", "CREDENTIAL", FIRST_PAGE).getTotalElements()).isEqualTo(1);
        assertThat(logs.searchAuditApiLogs("requesterId", "example", FIRST_PAGE).getTotalElements()).isEqualTo(1);
    }
}
