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

package org.omnione.did.issuer.v1.dto.vc;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.omnione.did.data.model.enums.vc.VcStatus;

/**
 * request to update the status of a Verifiable Credential.
 */
@Getter
@Setter
@ToString
public class UpdateVcStatusReqDto {
    @NotNull(message = "vcId cannot be null")
    private String vcId;
    @NotNull(message = "vcStatus cannot be null")
    private VcStatus vcStatus;
}
