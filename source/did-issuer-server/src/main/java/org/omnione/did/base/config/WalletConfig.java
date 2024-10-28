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

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.WalletProperty;
import org.omnione.did.crypto.exception.CryptoException;
import org.omnione.did.wallet.exception.WalletException;
import org.omnione.did.wallet.key.WalletManagerFactory;
import org.omnione.did.wallet.key.WalletManagerInterface;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

/**
 * This class represents the configuration for the Wallet.
 *
 * It is annotated with @Configuration to indicate that it is a configuration class
 * and @RequiredArgsConstructor to automatically generate a constructor with the required arguments.
 * The class has a single dependency on the WalletProperty object, which is injected via the constructor.
 *
 * The class defines a @Bean method, wallet(), which returns an instance of WalletManagerInterface.
 * The method connects to the wallet using the given walletProperty and returns the walletManager.
 * If any WalletException is thrown, it is caught and re-thrown as an OpenDidException with the error code
 * ErrorCode.WALLET_INFO_NOT_FOUND.
 */
@RequiredArgsConstructor
@Configuration
public class WalletConfig {
    private final WalletProperty walletProperty;
    private final Environment environment;
    @Bean
    public WalletManagerInterface wallet() {

        List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());

        if (!activeProfiles.contains("sample")) {
            try {
                WalletManagerInterface walletManager = WalletManagerFactory.getWalletManager(WalletManagerFactory.WalletManagerType.FILE, walletProperty.getFilePath());
                walletManager.connect(walletProperty.getFilePath(), walletProperty.getPassword()
                        .toCharArray());

                return walletManager;
            } catch (WalletException e) {
                throw new OpenDidException(ErrorCode.WALLET_INFO_NOT_FOUND);
            }
        }
        return new DummyWalletManager();
    }
}
