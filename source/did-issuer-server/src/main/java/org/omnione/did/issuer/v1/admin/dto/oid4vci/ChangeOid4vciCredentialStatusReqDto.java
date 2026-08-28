/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package org.omnione.did.issuer.v1.admin.dto.oid4vci;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.omnione.did.oid4vc.oid4vci.status.model.CredentialStatus;

@Getter
@Setter
@NoArgsConstructor
public class ChangeOid4vciCredentialStatusReqDto {
    private CredentialStatus status;
    private String reason;
}
