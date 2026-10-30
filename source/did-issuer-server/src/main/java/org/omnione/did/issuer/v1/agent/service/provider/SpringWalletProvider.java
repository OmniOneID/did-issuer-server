package org.omnione.did.issuer.v1.agent.service.provider;

import lombok.RequiredArgsConstructor;
import org.omnione.did.zkp.datamodel.credential.CredentialSignature;
import org.omnione.did.zkp.datamodel.credential.CredentialValues;
import org.omnione.did.zkp.datamodel.credential.SignatureCorrectnessProof;
import org.omnione.did.zkp.datamodel.credentialoffer.KeyCorrectnessProof;
import org.omnione.did.zkp.datamodel.credentialrequest.CredentialRequest;
import org.omnione.did.issuer.v1.agent.service.FileWalletService;
import org.omnione.did.issuer.v1.common.service.ZkpWalletService;
import org.omnione.did.sdk.issuer.provider.WalletProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringWalletProvider implements WalletProvider {

    private final FileWalletService fileWalletService;
    private final ZkpWalletService zkpWalletService;

    @Override
    public byte[] generateCompactSignature(String keyId, String source) {
        return fileWalletService.generateCompactSignature(keyId, source);
    }

    @Override
    public CredentialSignature credSignature(String alias, CredentialRequest credentialRequest,
            CredentialValues credentialValues) {
        return zkpWalletService.credSignature(alias, credentialRequest, credentialValues);
    }

    @Override
    public SignatureCorrectnessProof signatureCorrectnessProof(String alias, CredentialRequest credentialRequest,
            CredentialSignature credentialSignature) {
        return zkpWalletService.signatureCorrectnessProof(alias, credentialRequest, credentialSignature);
    }

    @Override
    public KeyCorrectnessProof getCorrectnessProof(String alias) {
        return zkpWalletService.getCorrectnessProof(alias);
    }
}
