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

package org.omnione.did.issuer.v1.controller;

import lombok.RequiredArgsConstructor;
import org.omnione.did.base.constants.UrlConstant;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.issuer.v1.dto.EnrollEntityResDto;
import org.omnione.did.issuer.v1.service.CertificateVcService;
import org.omnione.did.issuer.v1.service.EnrollEntityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The EnrollEntityController class is a controller that handles the enroll entity request.
 * It provides endpoints for enrolling an entity and requesting a certificate verifiable credential.
 *
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = UrlConstant.Issuer.V1)
public class EnrollEntityController {
    private final EnrollEntityService enrollEntityService;
    private final CertificateVcService certificateVcService;
    /**
     * Enroll an entity.
     *
     * @return the response to enrolling an entity
     */
    @PostMapping("/certificate-vc")
    public EnrollEntityResDto enrollEntity() {
        return enrollEntityService.enrollEntity();
    }

    /**
     * Request a certificate verifiable credential.
     *
     * @return the certificate verifiable credential
     */
    @GetMapping("/certificate-vc")
    public String requestCertificateVc() {
        return certificateVcService.requestCertificateVc();
    }
}
