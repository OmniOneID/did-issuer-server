package org.omnione.did.issuer.v1.common.service;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.ZkpWalletProperty;
import org.omnione.did.base.util.BaseWalletUtil;
import org.omnione.did.issuer.v1.agent.service.sample.ZkpSampleConstants;
import org.omnione.did.zkp.datamodel.credentialoffer.KeyCorrectnessProof;
import org.omnione.did.zkp.exception.ZkpException;
import org.omnione.did.zkp.wallet.key.ZkpWalletManagerInterface;
import org.springframework.stereotype.Service;

import java.util.List;

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
        this.zkpWalletManager = BaseWalletUtil.getZkpFileWalletManager();
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

        return BaseWalletUtil.initializeZkpWallet(zkpWalletProperty.getFilePath(), zkpWalletProperty.getPassword());
    }

    public void generateRandomZkpKey(String keyId, List<String> attrNames) {
        try {
            connectToZkpWallet();
            zkpWalletManager.generateRandomZkpKey(keyId, attrNames);
        } catch (ZkpException e) {
            log.error("Failed to Generate ZKP Key: {}", e.getMessage());
            e.printStackTrace();
            throw new OpenDidException(ErrorCode.CRYPTO_KEY_PAIR_GENERATION_FAILED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public KeyCorrectnessProof getCorrectnessProof(String credentialDefinitionKeyId) {
        try {
            byte[] zkpKeyProof = zkpWalletManager.generateZkpKeyProof(credentialDefinitionKeyId);

            return new Gson()
                    .fromJson(new String(zkpKeyProof), KeyCorrectnessProof.class);
        } catch (ZkpException e) {
            log.error("Failed to Generate Correctness Proof: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.FAILED_TO_GENERATE_CORRECTNESS_PROOF);
        }
    }
}
