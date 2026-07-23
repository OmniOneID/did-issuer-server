/*
 * Copyright 2026 OmniOne.
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
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_oid4vc_status_list")
public class Oid4vcStatusListEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "list_uri", nullable = false, unique = true, length = 512)
    private String listUri;

    @Column(name = "format", nullable = false, length = 64)
    private String format;

    @Column(name = "bits", nullable = false)
    private int bits = 2;

    @Column(name = "capacity", nullable = false)
    private long capacity = 100_000L;

    @Column(name = "next_index", nullable = false)
    private long nextIndex;

    @Column(name = "signing_key_id", nullable = false, length = 256)
    private String signingKeyId;

    @Column(name = "ttl_seconds", nullable = false)
    private long ttlSeconds = 300L;

    @Column(name = "list_version", nullable = false)
    private long listVersion;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;
}
