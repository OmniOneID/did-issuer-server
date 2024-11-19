/*
 * Copyright 2024 OmniOne.
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

package org.omnione.did.issuer.v1.service;

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.WalletProperty;
import org.omnione.did.base.util.BaseWalletUtil;
import org.omnione.did.wallet.exception.WalletException;
import org.omnione.did.wallet.key.WalletManagerInterface;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Service for managing wallet operations, including connection and signature generation.
 * This service interacts with a file-based wallet to sign and verify data for the DID system.
 */
@Service
@Slf4j
public class FileWalletService {
    private final WalletProperty walletProperty;
    private final WalletManagerInterface walletManager;

    public FileWalletService(WalletProperty walletProperty) {
        this.walletProperty = walletProperty;
        this.walletManager = BaseWalletUtil.getFileWalletManager();
    }

    /**
     * Connects to a file-based wallet.
     *
     * @throws OpenDidException if the connection fails.
     */
    public void connectToWallet() {
        try {
            walletManager.connect(walletProperty.getFilePath(), walletProperty.getPassword().toCharArray());
        } catch (WalletException e) {
            log.error("Failed to connect to wallet: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.WALLET_CONNECT_FAILURE);
        }
    }

    /**
     * Generates a compact signature for the given key and plain text data.
     *
     * @param keyId Key ID.
     * @param plainText Plain text data.
     * @return Generated signature.
     * @throws OpenDidException if signature generation fails.
     */
    public byte[] generateCompactSignature(String keyId, String plainText) {
        return generateCompactSignature(keyId, plainText.getBytes(StandardCharsets.UTF_8));
    }
    /**
     * Generates a compact signature for the given key and binary data.
     *
     * @param keyId Key ID.
     * @param plainText Binary data.
     * @return Generated signature.
     * @throws OpenDidException if signature generation fails.
     */
    public byte[] generateCompactSignature(String keyId, byte[] plainText) {
        try {
            if (!walletManager.isConnect()) {
                log.info("Wallet manager disConnect. Connecting to wallet...");
                connectToWallet();
            }

            byte[] signature = BaseWalletUtil.generateCompactSignature(walletManager, keyId, plainText);
            log.info("Compact signature generated for keyId: {}", keyId);
            return signature;
        } catch (OpenDidException e) {
            throw e;
        } catch (Exception e) {
            throw new OpenDidException(ErrorCode.SIGNATURE_GENERATION_FAILED);
        }
    }
}
