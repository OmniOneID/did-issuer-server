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

import lombok.RequiredArgsConstructor;
import org.omnione.did.crypto.exception.CryptoException;
import org.omnione.did.crypto.util.MultiBaseUtils;
import org.omnione.did.issuer.v1.agent.service.FileWalletService;
import org.omnione.did.oid4vc.formatter.oid4vci.generator.CompactSigner;
import org.omnione.did.oid4vc.formatter.oid4vci.generator.dto.IssuerKeyInfo;
import org.omnione.did.oid4vc.formatter.util.SignatureUtil;
import org.omnione.did.oid4vc.oid4vci.property.IssuerProperties;
import org.omnione.did.oid4vc.oid4vci.service.KeyDataProvider;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class MockKeyDataProvider implements KeyDataProvider {

    private final IssuerProperties issuerProperties;
    private final FileWalletService fileWalletService;

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
                byte[] signature = fileWalletService.generateCompactSignature(keyId, hash);
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
        if (credentialType.equals("mDL"))
            keyInfo.setCredentialSchemaUrl("org.iso.18013.5.1.mDL");
        else if (credentialType.equals("mDocPID"))
            keyInfo.setCredentialSchemaUrl("eu.europa.ec.eudi.pid.1");
        else
            keyInfo.setCredentialSchemaUrl("urn:eudi:pid:1"); // "https://credentials.gov.kr/identity_credential"
        keyInfo.setPublicKey(PUBLIC_KEY);

        return keyInfo;
    }

}
