/*
 * Copyright 2024 - 2025 OmniOne.
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

package org.omnione.did.issuer.v1.agent.service;

import org.omnione.did.issuer.v1.agent.service.provider.SpringVcStatusDataProvider;
import org.omnione.did.sdk.issuer.protocol.vc.VcStatusProtocol;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.omnione.did.issuer.v1.agent.service.query.TransactionService;

/**
 * Implementation of the VcStatusService interface.
 * Extends the SDK Core Component VcStatusManager.
 */
@Service
@Transactional
@Profile("!sample")
public class VcStatusServiceImpl extends AbstractPersistentVcStatusService {

    public VcStatusServiceImpl(
            VcStatusProtocol vcStatusProtocol,
            SpringVcStatusDataProvider vcStatusDataProvider,
            TransactionService transactionService) {
        super(vcStatusProtocol, vcStatusDataProvider, transactionService);
    }
}
