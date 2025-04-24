/*
 * Copyright 2025 OmniOne.
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
package org.omnione.did.issuer.v1.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for parsing JSON strings into Map representations in the Admin Console.
 * <p>
 * This service supports parsing Certificate VC and DID Document JSON structures,
 * and throws standardized OpenDID exceptions when parsing fails.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JsonParseService {

    /**
     * Parses a certificate VC JSON string into a Map.
     *
     * @param certificateJson the JSON string representing the certificate VC
     * @return a Map containing the parsed data
     * @throws OpenDidException if the JSON format is invalid
     */
    public Map<String, Object> parseCertificateVcToMap(String certificateJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(certificateJson, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Certificate VC JSON (invalid format): {}", certificateJson, e);
            throw new OpenDidException(ErrorCode.INVALID_CERTIFICATE_VC_JSON_FORMAT);
        } catch (Exception e) {
            log.error("Unexpected error while parsing Certificate VC JSON", e);
            throw new OpenDidException(ErrorCode.INVALID_CERTIFICATE_VC_JSON_FORMAT);
        }
    }

    /**
     * Parses a DID Document JSON string into a Map.
     *
     * @param didDocJson the JSON string representing the DID Document
     * @return a Map containing the parsed DID Document
     * @throws OpenDidException if the JSON format is invalid
     */
    public Map<String, Object> parseDidDocToMap(String didDocJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(didDocJson, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse DID Document JSON (invalid format): {}", didDocJson, e);
            throw new OpenDidException(ErrorCode.INVALID_DID_DOCUMENT);
        } catch (Exception e) {
            log.error("Unexpected error while parsing DID Document JSON", e);
            throw new OpenDidException(ErrorCode.INVALID_DID_DOCUMENT);
        }
    }
}
