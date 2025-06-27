package org.omnione.did.issuer.v1.agent.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.data.model.schema.MetaData;
import org.omnione.did.data.model.schema.SchemaClaims;
import org.omnione.did.data.model.schema.SchemaCredentialSubject;
import org.omnione.did.issuer.v1.admin.service.query.NamespaceQueryService;
import org.omnione.did.issuer.v1.admin.service.query.VcSchemaQueryService;
import org.omnione.did.issuer.v1.agent.service.query.IssuerInfoQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class VcServiceHelper {
    private final VcSchemaQueryService vcSchemaQueryService;
    private final NamespaceQueryService namespaceQueryService;
    private final IssuerInfoQueryService issuerInfoQueryService;

    public String getVcSchemaJsonBySchemaId(String schemaId) {
        return getVcSchemaBySchemaId(schemaId).toJson();
    }

    public org.omnione.did.data.model.schema.VcSchema getVcSchemaBySchemaId(String schemaId) {
        VcSchema vcSchemaEntity = vcSchemaQueryService.findByVcSchemaId(schemaId);

        MetaData metaData = createMetaData(vcSchemaEntity);
        List<SchemaClaims> schemaClaimsList = createSchemaClaimsList(vcSchemaEntity.getId());

        SchemaCredentialSubject credentialSubject = new SchemaCredentialSubject();
        credentialSubject.setClaims(schemaClaimsList);

        return createVcSchema(vcSchemaEntity, metaData, credentialSubject);
    }

    private MetaData createMetaData(VcSchema vcSchemaEntity) {
        MetaData metaData = new MetaData();
        metaData.setFormatVersion(vcSchemaEntity.getVersion());
        metaData.setLanguage(vcSchemaEntity.getLanguage());
        return metaData;
    }

    private List<SchemaClaims> createSchemaClaimsList(Long vcSchemaId) {
        List<Long> namespaceIdList = vcSchemaQueryService.findRelationByVcSchemaId(vcSchemaId);
        List<Namespace> namespaceList = namespaceQueryService.findAllById(namespaceIdList);

        return namespaceList.stream().map(Namespace::getSchemaClaims)
                .collect(Collectors.toList());
    }

    private org.omnione.did.data.model.schema.VcSchema createVcSchema(
            VcSchema vcSchemaEntity, MetaData metaData, SchemaCredentialSubject credentialSubject) {

        org.omnione.did.data.model.schema.VcSchema vcSchema = new org.omnione.did.data.model.schema.VcSchema();
        vcSchema.setId(issuerInfoQueryService.getIssuerInfo().getServerUrl() + "/api/v1/vc/vcschema?name=" + vcSchemaEntity.getVcSchemaId()); // TODO: URL setting
        vcSchema.setSchema("https://opendid.org/schema/vc.osd");
        vcSchema.setDescription(vcSchemaEntity.getDescription());
        vcSchema.setTitle(vcSchemaEntity.getTitle());
        vcSchema.setMetadata(metaData);
        vcSchema.setCredentialSubject(credentialSubject);

        return vcSchema;
    }

}
