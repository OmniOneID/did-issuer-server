/*
 * Copyright 2024 - 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.omnione.did.issuer.v1.agent.service.query;


import org.omnione.did.base.db.domain.Namespace;
import org.omnione.did.base.db.domain.VcSchema;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.data.model.schema.MetaData;
import org.omnione.did.data.model.schema.SchemaClaims;
import org.omnione.did.data.model.schema.SchemaCredentialSubject;
import org.omnione.did.issuer.v1.admin.service.query.NamespaceQueryService;
import org.omnione.did.issuer.v1.admin.service.query.VcSchemaQueryService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The VcSchemaService class provides methods for retrieving VC schemas.
 * VcSchema is a schema that defines the structure of a Verifiable Credential (VC).
 */
@Profile("!sample")
@Service
public class VcSchemaService {

    private final NamespaceQueryService namespaceQueryService;
    private final VcSchemaQueryService vcSchemaQueryService;
    private final IssuerInfoQueryService issuerInfoQueryService;

    public VcSchemaService(NamespaceQueryService namespaceQueryService, VcSchemaQueryService vcSchemaQueryService,
                           IssuerInfoQueryService issuerInfoQueryService) {
        this.namespaceQueryService = namespaceQueryService;
        this.vcSchemaQueryService = vcSchemaQueryService;
        this.issuerInfoQueryService = issuerInfoQueryService;
    }

    /**
     * Returns the schema for the specified VC.
     *
     * @param name the name of the VC
     * @return the schema for the specified VC
     * @throws OpenDidException if the VC schema name is invalid
     */
    public String getVcSchemaByName(String name) {
        VcSchema vcSchemaEntity = vcSchemaQueryService.findByVcSchemaId(name);

        MetaData metaData = createMetaData(vcSchemaEntity);
        List<SchemaClaims> schemaClaimsList = createSchemaClaimsList(vcSchemaEntity.getId());

        SchemaCredentialSubject credentialSubject = new SchemaCredentialSubject();
        credentialSubject.setClaims(schemaClaimsList);

        return createVcSchema(vcSchemaEntity, metaData, credentialSubject).toJson();
    }

    public org.omnione.did.data.model.schema.VcSchema getVcSchemaById(Long id) {
        VcSchema vcSchemaEntity = vcSchemaQueryService.findById(id);

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
