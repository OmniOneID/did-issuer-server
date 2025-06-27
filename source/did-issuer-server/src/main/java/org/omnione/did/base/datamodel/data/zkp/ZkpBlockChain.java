package org.omnione.did.base.datamodel.data.zkp;

import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;

import java.util.List;

public class ZkpBlockChain {

    private static ZkpBlockChain instance;

    private CredentialSchema credentialSchema;
    private CredentialDefinition credentialDefinition;


    private ZkpBlockChain() {}

    public static synchronized ZkpBlockChain getInstance() {
        if (instance == null) {
            instance = new ZkpBlockChain();
        }
        return instance;
    }


    public CredentialDefinition getCredentialDefinition(String credDefId) {
        return credentialDefinition;
    }

    public void setCredentialDefinition(CredentialDefinition credentialDefinition) {
        this.credentialDefinition = credentialDefinition;
    }

    public CredentialSchema getCredentialSchema() {
        List<String> schemaList = credentialSchema.getAttrNames();
//        schemaList.remove("master_secret");
        schemaList.remove("masterSecret");
        credentialSchema.setAttrNames(schemaList);
        return credentialSchema;
    }

    public void setCredentialSchema(CredentialSchema credentialSchema) {
        this.credentialSchema = credentialSchema;
    }

}
