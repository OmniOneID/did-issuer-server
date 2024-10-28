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

package org.omnione.did.issuer.v1.api;


import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.issuer.v1.api.dto.DidDocApiResDto;
import org.omnione.did.issuer.v1.api.dto.UpdateVcStatusApiReqDto;
import org.omnione.did.issuer.v1.api.dto.VcMetaApiResDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.cloud.openfeign.FeignClient;


/**
 * RepositoryFeign
 * This is a Feign client that communicates with the Storage API.
 */
@FeignClient(value = "Storage", url = "http://127.0.0.1:8097/repository", path = "/api/v1")
public interface RepositoryFeign {
    /**
     * getDid
     * @param did String
     * @return DidDocApiResDto
     */
    @GetMapping("/did-doc")
    DidDocApiResDto getDid(@RequestParam(name = "did") String did);
    /**
     * getVcMetaData
     * @param vcId String
     * @return VcMetaApiResDto
     */
    @GetMapping("/vc-meta")
    VcMetaApiResDto getVcMetaData(@RequestParam(name = "vcId") String vcId);
    /**
     * inputVcMeta
     * @param vcMeta VcMeta
     */
    @PostMapping("/vc-meta")
    void inputVcMeta(@RequestBody VcMeta vcMeta);
    /**
     * updateVcStatus
     * @param request UpdateVcStatusApiReqDto
     */
    @PutMapping("/vc-meta")
    void updateVcStatus(@RequestBody UpdateVcStatusApiReqDto request);
}
