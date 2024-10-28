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

package org.omnione.did.issuer.v1.service.query;

import org.omnione.did.base.constants.VcPlanId;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The VcSchemaService class provides methods for retrieving VC schemas.
 * VcSchema is a schema that defines the structure of a Verifiable Credential (VC).
 */
@Service
public class VcSchemaService {

    @Value("${issue.domain}")
    private String ISSUER_DOMAIN;
    /**
     * Returns the schema for the specified VC.
     *
     * @param name the name of the VC
     * @return the schema for the specified VC
     * @throws OpenDidException if the VC schema name is invalid
     */
    public String getVcSchemaByName(String name) {
        if (VcPlanId.VCPLANID000000000001.getName().equals(name)) {
            return getMdlVcSchema();
        } else if (VcPlanId.VCPLANID000000000002.getName().equals(name)) {
            return getNationalIdSchema();
        }

        throw new OpenDidException(ErrorCode.VC_SCHEMA_NAME_INVALID);
    }

    /**
     * Returns the schema for the mobile driver's license VC.
     *
     * @return the schema for the mobile driver's license VC
     */

    public String getMdlVcSchema() {
        return """
                {
                  "@id": "{ISSUER}/api/v1/vc/vcschema?name=mdl",
                  "@schema": "https://opendid.org/schema/vc.osd",
                  "title": "OpenDID Mobile Driver License",
                  "description": "VC-formatted OpenDID Mobile License Driver.",
                  "metadata": {
                    "language": "ko",
                    "formatVersion": "1.0"
                  },
                  "credentialSubject": {
                    "claims": [
                      {
                        "namespace": {
                          "id": "org.iso.18013.5",
                          "name": "ISO/IEC 18013-5:2021 - Personal identification",
                          "ref": "https://www.iso.org/standard/69084.html"
                        },
                        "items": [
                          {
                            "id": "family_name",
                            "caption": "Family Name",
                            "type": "text",
                            "format": "plain"
                          },
                          {
                            "id": "given_name",
                            "caption": "Given Name",
                            "type": "text",
                            "format": "plain"
                          },
                          {
                            "id": "birth_date",
                            "caption": "Birth date",
                            "type": "text",
                            "format": "plain"
                          },
                          {
                            "id": "address",
                            "caption": "Address",
                            "type": "text",
                            "format": "plain"
                          },
                          {
                            "id": "document_number",
                            "caption": "Document Number",
                            "type": "text",
                            "format": "plain"
                          },
                          {
                            "id": "issue_date",
                            "caption": "Issue Date",
                            "type": "text",
                            "format": "plain"
                          }
                        ]
                      },
                      {
                        "namespace": {
                          "id": "org.opendid.v1",
                          "name": "OpenDID v1",
                          "ref": "https://opendid.org/schema/v1/claim"
                        },
                        "items": [
                          {
                            "id": "pii",
                            "caption": "PII",
                            "type": "text",
                            "format": "plain",
                            "hideValue": false,
                            "description": "국가마다 서로 다른 개인식별자로서 한국은 CI를 사용함"
                          } ]
                      } ]
                  }
                }
                """.replaceAll("\\{ISSUER}", ISSUER_DOMAIN);
    }

    /**
     * Returns the schema for the national ID VC.
     *
     * @return the schema for the national ID VC
     */
    public String getNationalIdSchema() {
        return """
                {
                  "@id": "{ISSUER}/api/v1/vc/vcschema?name=national_id",
                  "@schema": "https://opendid.org/schema/vc.osd",
                  "title": "OpenDID National ID",
                  "description": "VC-formatted OpenDID National ID.",
                  "metadata": {
                    "language": "ko",
                    "formatVersion": "1.0"
                  },
                  "credentialSubject": {
                    "claims": [
                      {
                        "namespace": {
                          "id": "org.opendid.v1.national_id",
                          "name": "OpenDID National ID",
                          "ref": "https://opendid.org/schema/v1/claim"
                        },
                        "items": [
                          {
                            "id": "user_name",
                            "caption": "Name",
                            "type": "text",
                            "format": "plain",
                            "location": "inline"
                          },
                          {
                            "id": "birth_date",
                            "caption": "Birth date",
                            "type": "text",
                            "format": "plain",
                            "location": "inline"
                          },
                          {
                            "id": "issue_date",
                            "caption": "Issue Date",
                            "type": "text",
                            "format": "plain",
                            "location": "inline"
                          },
                          {
                            "id": "address",
                            "caption": "Address",
                            "type": "text",
                            "format": "plain",
                            "location": "inline"
                          }
                        ]
                      }
                    ]
                  }
                }
                """.replaceAll("\\{ISSUER}", ISSUER_DOMAIN);

    }
}
