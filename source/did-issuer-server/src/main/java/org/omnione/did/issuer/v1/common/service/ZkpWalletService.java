package org.omnione.did.issuer.v1.common.service;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.ZkpWalletProperty;
import org.omnione.did.zkp.datamodel.credential.CredentialSignature;
import org.omnione.did.zkp.datamodel.credential.CredentialValues;
import org.omnione.did.zkp.datamodel.credential.PrimaryCredentialSignature;
import org.omnione.did.zkp.datamodel.credential.SignatureCorrectnessProof;
import org.omnione.did.zkp.datamodel.credentialoffer.KeyCorrectnessProof;
import org.omnione.did.zkp.datamodel.credentialrequest.CredentialRequest;
import org.omnione.did.zkp.exception.ZkpException;
import org.omnione.did.zkp.wallet.key.ZkpWalletManagerInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import org.omnione.did.zkp.wallet.enums.ZkpWalletEncryptType;
import org.omnione.did.zkp.wallet.key.ZkpWalletManagerFactory;
import static org.omnione.did.zkp.exception.ZkpErrorCode.ERR_CODE_ZKP_WALLET_ALREADY_FILE;

/**
 * Description...
 */
@Slf4j
@Service
public class ZkpWalletService {

    private final ZkpWalletProperty zkpWalletProperty;
    @Getter
    private final ZkpWalletManagerInterface zkpWalletManager;


    public ZkpWalletService(ZkpWalletProperty zkpWalletProperty) {
        this.zkpWalletProperty = zkpWalletProperty;
        this.zkpWalletManager = getZkpFileWalletManager();
    }

    public void connectToZkpWallet() {
        try {
            zkpWalletManager.connect(zkpWalletProperty.getFilePath(), zkpWalletProperty.getPassword().toCharArray());
        } catch (ZkpException e) {
            log.error("Failed to connect to zkp wallet: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.ZKP_WALLET_CONNECT_FAILURE);
        }
    }

    public ZkpWalletManagerInterface initializeZkpWallet() {

        return initializeZkpWalletFile(zkpWalletProperty.getFilePath(), zkpWalletProperty.getPassword());
    }

    public void generateRandomZkpKey(String keyId, List<String> attrNames) {
        try {
            connectToZkpWallet();
            zkpWalletManager.generateRandomZkpKey(keyId, attrNames);
        } catch (ZkpException e) {
            log.error("Failed to Generate ZKP Key: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.CRYPTO_KEY_PAIR_GENERATION_FAILED);
        } catch (Exception e) {
            log.error("Failed to Generate ZKP Key: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.CRYPTO_KEY_PAIR_GENERATION_FAILED);
        }
    }

    public CredentialSignature credSignature(String alias, CredentialRequest credentialRequest, CredentialValues credentialValues) {
        try {
            connectToZkpWallet();
            byte[] zkpSignature = zkpWalletManager.generateZkpSignature(alias, credentialRequest, credentialValues);
            PrimaryCredentialSignature pCredSignature = new Gson().fromJson(new String(zkpSignature), PrimaryCredentialSignature.class);

            CredentialSignature credSignature = new CredentialSignature();
            credSignature.setPrimaryCredential(pCredSignature);

            return credSignature;
        } catch (ZkpException ex) {
            throw new OpenDidException(ErrorCode.FAILED_TO_CREDENTIAL_SIGNATURE);
        }
    }

    public SignatureCorrectnessProof signatureCorrectnessProof(String alias, CredentialRequest credentialRequest, CredentialSignature credSignature) {
        try {
            connectToZkpWallet();
            byte[] zkpSignatureProof = zkpWalletManager.generateZkpSignatureProof(alias, credSignature, credentialRequest.getNonce());
            return new Gson().fromJson(new String(zkpSignatureProof), SignatureCorrectnessProof.class);
        } catch (ZkpException ex) {
            throw new OpenDidException(ErrorCode.FAILED_TO_SIGNATURE_CORRECTNESS_PROOF);
        }
    }

    public KeyCorrectnessProof getCorrectnessProof(String alias) {
        try {
            connectToZkpWallet();
            byte[] zkpKeyProof = zkpWalletManager.generateZkpKeyProof(alias);

            return new Gson()
                    .fromJson(new String(zkpKeyProof), KeyCorrectnessProof.class);
        } catch (ZkpException e) {
            log.error("Failed to Generate Correctness Proof: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.FAILED_TO_GENERATE_CORRECTNESS_PROOF);
        }
    }

    public void deleteZkpKeyByAlias(String alias) {
        try {
            zkpWalletManager.removeZkpKey(alias);
        } catch (ZkpException e) {
            log.error("Failed to Remove ZKP Key: {}", e.getMessage());
        }
    }

    private static ZkpWalletManagerInterface getZkpFileWalletManager() {
        try {
            return ZkpWalletManagerFactory.getZkpWalletManager(ZkpWalletManagerFactory.ZkpWalletManagerType.FILE);
        } catch (ZkpException e) {
            log.error("Failed to get Wallet Manager: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.FAILED_TO_GET_FILE_WALLET_MANAGER);
        }
    }

    private static ZkpWalletManagerInterface initializeZkpWalletFile(String zkpWalletPath, String password) {
        createZkpFileWalletSafe(zkpWalletPath, password);
        ZkpWalletManagerInterface zkpWalletManager = connectZkpFileWallet(zkpWalletPath, password);

        return zkpWalletManager;
    }

    private static ZkpWalletManagerInterface connectZkpFileWallet(String zkpWalletPath, String password) {
        try {
            ZkpWalletManagerInterface zkpWalletManager = ZkpWalletManagerFactory.getZkpWalletManager(ZkpWalletManagerFactory.ZkpWalletManagerType.FILE);
            zkpWalletManager.connect(zkpWalletPath, password.toCharArray());

            return zkpWalletManager;
        } catch (ZkpException e) {
            log.error("Failed to connect zkp wallet: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.ZKP_WALLET_CONNECT_FAILURE);
        }
    }

    private static void createZkpFileWallet(String zkpWalletPath, String password) {
        try {
            ZkpWalletManagerInterface zkpWalletManager = ZkpWalletManagerFactory.getZkpWalletManager(ZkpWalletManagerFactory.ZkpWalletManagerType.FILE);
            zkpWalletManager.create(zkpWalletPath, password.toCharArray(), ZkpWalletEncryptType.AES_256_CBC_PKCS5Padding);
        } catch (ZkpException e) {
            if (ERR_CODE_ZKP_WALLET_ALREADY_FILE.getCode().equals(e.getErrorCode())) {
                log.error("ZKP Wallet already exists: {}", e.getMessage());
                throw new OpenDidException(ErrorCode.ZKP_WALLET_ALREADY_EXISTS);
            }

            log.error("Failed to create zkp wallet: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.ZKP_WALLET_CONNECT_FAILURE);
        }
    }

    private static void createZkpFileWalletSafe(String zkpWalletPath, String password) {
        try {
            createZkpFileWallet(zkpWalletPath, password);
        } catch (OpenDidException e) {
            if (e.getErrorCode() == ErrorCode.ZKP_WALLET_ALREADY_EXISTS) {
                log.info("Wallet already exists at {}.", zkpWalletPath);
            } else {
                throw e;
            }
        }
    }
}
