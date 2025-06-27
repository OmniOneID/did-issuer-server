package org.omnione.did.issuer.v1.common.service;

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("sample")
public class MockStorageServiceImpl implements StorageService {

    @Override
    public DidDocument findDidDoc(String didKeyUrl) {
        return null;
    }

    @Override
    public void registerVcMeta(VcMeta vcMeta) {

    }

    @Override
    public void updateVcStatus(String vcId, VcStatus vcStatus) {

    }

    @Override
    public VcMeta getVcMetaByVcId(String vcId) {
        return null;
    }

    @Override
    public void registerCredentialSchema(CredentialSchema credentialSchema) {

    }

    @Override
    public void registerCredentialDefinition(CredentialDefinition credentialDefinition) {

    }

    @Override
    public void registerVcSchema(VcSchema vcSchema, String did) {

    }

    @Override
    public VcSchema getVcSchema(String vcSchemaId) {
        return null;
    }

    @Override
    public CredentialSchema getCredentialSchema(String credentialSchemaId) {
        return null;
    }

    @Override
    public CredentialDefinition getCredentialDefinition(String credentialDefinitionId) {
        return null;
    }
}
