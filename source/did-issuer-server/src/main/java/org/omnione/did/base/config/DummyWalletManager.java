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

package org.omnione.did.base.config;

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.wallet.enums.WalletEncryptType;
import org.omnione.did.wallet.exception.WalletException;
import org.omnione.did.wallet.key.WalletManagerInterface;
import org.omnione.did.wallet.key.data.CryptoKeyPairInfo;
import org.omnione.did.wallet.key.data.CryptoKeyPairInfo.KeyAlgorithmType;
import org.omnione.did.wallet.key.data.KeyElement;

import java.util.Collections;
import java.util.List;


/**
 * DummyWalletManager is a placeholder implementation of the WalletManagerInterface.
 *
 * <p>This class is intended to provide a default, non-functional implementation of the
 * WalletManagerInterface for cases where the actual wallet manager is not required,
 * such as when a specific Spring profile is active (e.g., "sample").</p>
 *
 * <p>The methods in this class do not perform any real operations and return either
 * default values or empty results. This class is primarily used to satisfy the
 * dependency injection requirements without executing any meaningful logic.</p>
 *
 * <p>Use this class only when an instance of WalletManagerInterface is needed but
 * no real wallet operations should be performed.</p>
 *
 */
@Slf4j
public class DummyWalletManager implements WalletManagerInterface {

    @Override
    public void create(String s, char[] chars, WalletEncryptType walletEncryptType) throws WalletException {
        log.warn("DummyWalletManager: create() method called. No operation performed.");
    }

    @Override
    public void connect(String s, char[] chars) throws WalletException {
        log.warn("DummyWalletManager: connect() method called. No operation performed.");
    }

    @Override
    public void changePassword(char[] chars, char[] chars1) throws WalletException {
        log.warn("DummyWalletManager: changePassword() method called. No operation performed.");
    }

    @Override
    public boolean disConnect() {
        log.warn("DummyWalletManager: disConnect() method called. Always returning false.");
        return false;
    }

    @Override
    public boolean isConnect() {
        log.warn("DummyWalletManager: isConnect() method called. Always returning false.");
        return false;
    }

    @Override
    public void addKey(CryptoKeyPairInfo cryptoKeyPairInfo) throws WalletException {
        log.warn("DummyWalletManager: addKey() method called. No operation performed.");
    }

    @Override
    public void generateRandomKey(String s, KeyAlgorithmType keyAlgorithmType) throws WalletException {
        log.warn("DummyWalletManager: generateRandomKey() method called. No operation performed.");
    }

    @Override
    public boolean isExistKey(String s) throws WalletException {
        log.warn("DummyWalletManager: isExistKey() method called. Always returning false.");
        return false;
    }

    @Override
    public String getPublicKey(String s) throws WalletException {
        log.warn("DummyWalletManager: getPublicKey() method called. Always returning null.");
        return null;
    }

    @Override
    public String getKeyAlgorithm(String s) throws WalletException {
        log.warn("DummyWalletManager: getKeyAlgorithm() method called. Always returning null.");
        return null;
    }

    @Override
    public KeyElement getKeyElement(String s) throws WalletException {
        log.warn("DummyWalletManager: getKeyElement() method called. Always returning null.");
        return null;
    }

    @Override
    public List<String> getKeyIdList() throws WalletException {
        log.warn("DummyWalletManager: getKeyIdList() method called. Always returning empty list.");
        return Collections.emptyList();
    }

    @Override
    public void removeKey(String s) throws WalletException {
        log.warn("DummyWalletManager: removeKey() method called. No operation performed.");
    }

    @Override
    public void removeAllKeys() throws WalletException {
        log.warn("DummyWalletManager: removeAllKeys() method called. No operation performed.");
    }

    @Override
    public byte[] getSharedSecret(String s, String s1) throws WalletException {
        log.warn("DummyWalletManager: getSharedSecret() method called. Always returning empty byte array.");
        return new byte[0];
    }

    @Override
    public byte[] generateCompactSignatureFromHash(String s, byte[] bytes) throws WalletException {
        log.warn("DummyWalletManager: generateCompactSignatureFromHash() method called. Always returning empty byte array.");
        return new byte[0];
    }
}
