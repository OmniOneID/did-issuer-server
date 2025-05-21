package org.omnione.did.issuer.v1.admin.service;

import lombok.RequiredArgsConstructor;
import org.omnione.did.issuer.v1.common.service.StorageService;
import org.omnione.did.issuer.v1.common.service.ZkpWalletService;
import org.omnione.did.zkp.core.manager.ZkpCredentialMetadataManager;
import org.omnione.did.zkp.crypto.keypair.CredentialPrimaryPublicKey;
import org.omnione.did.zkp.datamodel.definition.CredentialDefinition;
import org.omnione.did.zkp.datamodel.schema.CredentialSchema;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;
import org.omnione.did.zkp.exception.ZkpException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Description...
 */
@RequiredArgsConstructor
@Service
public class ZkpServiceSample {

    private final ZkpWalletService zkpWalletService;
    private final StorageService storageService;
    public CredentialSchema createCredentialSchema() {


        return null;
    }

    public CredentialDefinition createCredentialDefinition() {
        // TODO: CredentialSchema 조회

        CredentialSchema credentialSchema = GsonWrapper.getGson().fromJson(test, CredentialSchema.class);
//        CredentialSchema credentialSchema = storageService.getCredentialSchema("credentialId");  // TODO: Use This
        try {
            System.out.println("credentialSchema.getAttrNames() = " + credentialSchema.getAttrNames());
            zkpWalletService.initializeZkpWallet();
            zkpWalletService.generateRandomZkpKey("mdl", credentialSchema.getAttrNames()); // TODO: getKeyID


            CredentialPrimaryPublicKey mdl = zkpWalletService.getZkpWalletManager().getCredentialPrimaryPublicKey("mdl");
            CredentialDefinition definition = new ZkpCredentialMetadataManager().createDefinition("did:omn:issuer", credentialSchema, mdl);

            // TODO: Save TO B/C
            System.out.println("GsonWrapper.getGson().toJson(definition) = " + GsonWrapper.getGsonPrettyPrinting().toJson(definition));
            storageService.registerCredentialDefinition(definition);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }


    private String test = """
            {
              "id": "did:omn:NcYxiDXkpYi6ov5FcYDi1e:2:mdl:1.0",
              "name": "mdl",
              "version": "1.0",
              "attrNames": [
                "MDLNS.zkpsex",
                "MDLNS.zkpbirth",
                "MDLNS.zkpasort",
                "MDLNS.zkpaddr"
              ],
              "attrTypes": [
                {
                  "namespace": {
                    "id": "MDLNS",
                    "name": "MDL NameSpace"
                  },
                  "items": [
                    {
                      "label": "zkpsex",
                      "caption": "성별",
                      "type": "STRING"
                    },
                    {
                      "label": "zkpbirth",
                      "caption": "생년월일",
                      "type": "NUMBER"
                    },
                    {
                      "label": "zkpasort",
                      "caption": "뭔지모름",
                      "type": "STRING"
                    },
                    {
                      "label": "zkpaddr",
                      "caption": "주소",
                      "type": "STRING",
                      "i18n": {
                        "ko": "주소",
                        "en": "address"
                      }
                    }
                  ]
                }
              ],
              "tag": "Tag1"
            }
            """;
}
