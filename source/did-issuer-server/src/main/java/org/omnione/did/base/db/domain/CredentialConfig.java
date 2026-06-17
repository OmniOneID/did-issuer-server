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

package org.omnione.did.base.db.domain;

import jakarta.persistence.*;
import lombok.*;
import org.omnione.did.base.db.converter.StringListConverter;

import java.io.Serializable;
import java.util.List;

/**
 * Entity class for the credential_config table.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "credential_config")
public class CredentialConfig extends BaseEntity implements Serializable {
    @Id
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "format", nullable = false, length = 50)
    private String format;

    @Convert(converter = StringListConverter.class)
    @Column(name = "identifiers", length = 200)
    private List<String> identifiers;

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @Builder.Default
    @Column(name = "enabled")
    private Boolean enabled = true;
}
