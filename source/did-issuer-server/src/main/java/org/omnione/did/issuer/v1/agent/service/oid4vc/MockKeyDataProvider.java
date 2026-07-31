/*
 * Copyright 2026 OmniOne.
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

package org.omnione.did.issuer.v1.agent.service.oid4vc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.omnione.did.crypto.exception.CryptoException;
import org.omnione.did.crypto.util.MultiBaseUtils;
import org.omnione.did.base.datamodel.data.oid4vci.SdkCredentialConfig;
import org.omnione.did.issuer.v1.agent.service.FileWalletService;
import org.omnione.did.issuer.v1.admin.service.Oid4vciConfigService;
import org.omnione.did.oid4vc.formatter.oid4vci.generator.CompactSigner;
import org.omnione.did.oid4vc.formatter.oid4vci.generator.dto.IssuerKeyInfo;
import org.omnione.did.oid4vc.formatter.util.SignatureUtil;
import org.omnione.did.oid4vc.oid4vci.service.KeyDataProvider;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MockKeyDataProvider implements KeyDataProvider {

    private final FileWalletService fileWalletService;
    private final Oid4vciConfigService oid4vciConfigService;

    @Override
    public IssuerKeyInfo getKeyInfo(String userId, String credentialType) {
        // test용 키쌍
        String PUBLIC_KEY = null;
        try {
            PUBLIC_KEY = Base64.getEncoder().encodeToString(MultiBaseUtils.decode(fileWalletService.getPublicKeyByKid("assert")));
        } catch (CryptoException e) {
            throw new RuntimeException(e);
        }

        // Add server wallet integration
        CompactSigner signer = (keyId, hash) -> {
                byte[] signature = fileWalletService.generateCompactSignatureByHash(keyId, hash);
                System.out.println("signature length : " + signature.length);
                //65 -> 64 (open did wallet의 개인키 필수)
                byte[] convertSignature = SignatureUtil.convertSignature(signature);
                System.out.println("convert signature length : " + convertSignature.length);

                return convertSignature;
        };

        IssuerKeyInfo keyInfo = new IssuerKeyInfo();
        keyInfo.setCompactSigner(signer);
        keyInfo.setIssuerKid("did:omn:issuer?versionId=1#assert");
        keyInfo.setIssuerKeyAlgorithm("Secp256r1");
        // Status List JWT signing only needs the signing key information.
        // It is an internal SDK credential type and has no credential configuration.
        if (!isStatusListCredentialType(credentialType)) {
            keyInfo.setCredentialSchemaUrl(resolveCredentialSchemaUrl(credentialType));
        }
        keyInfo.setPublicKey(PUBLIC_KEY);

        return keyInfo;
    }

    String resolveCredentialSchemaUrl(String credentialType) {
        try {
            Map<String, SdkCredentialConfig> configs = oid4vciConfigService.getSdkCredentialConfigs();
            SdkCredentialConfig config = findCredentialConfig(configs, credentialType);
            if (config == null) {
                throw new IllegalArgumentException(
                        "Credential configuration not found for credentialType: " + credentialType);
            }

            JsonObject metadata = JsonParser.parseString(config.getMetadataJson()).getAsJsonObject();
            String identifierField = isMdocFormat(config.getFormat()) ? "doctype" : "vct";
            if (!metadata.has(identifierField)
                    || metadata.get(identifierField).isJsonNull()
                    || metadata.get(identifierField).getAsString().isBlank()) {
                throw new IllegalStateException(
                        "Credential configuration '" + credentialType
                                + "' does not contain a valid " + identifierField);
            }
            return metadata.get(identifierField).getAsString();
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load credential configuration for credentialType: " + credentialType, e);
        }
    }

    private SdkCredentialConfig findCredentialConfig(
            Map<String, SdkCredentialConfig> configs, String credentialType) {
        SdkCredentialConfig configById = configs.get(credentialType);
        if (configById != null) {
            return configById;
        }

        List<SdkCredentialConfig> matchingConfigs = configs.values().stream()
                .filter(config -> config.getIdentifiers() != null
                        && config.getIdentifiers().contains(credentialType))
                .toList();
        if (matchingConfigs.size() > 1) {
            throw new IllegalStateException(
                    "Multiple credential configurations found for credentialType: " + credentialType);
        }
        return matchingConfigs.isEmpty() ? null : matchingConfigs.get(0);
    }

    boolean isStatusListCredentialType(String credentialType) {
        return "StatusList".equals(credentialType);
    }

    private boolean isMdocFormat(String format) {
        return format != null && format.toLowerCase().contains("mdoc");
    }

}
