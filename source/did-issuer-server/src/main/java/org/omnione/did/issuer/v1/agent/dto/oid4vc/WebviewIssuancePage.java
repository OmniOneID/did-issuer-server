/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */
package org.omnione.did.issuer.v1.agent.dto.oid4vc;

import java.util.List;

public record WebviewIssuancePage(
        String sessionToken,
        String csrfToken,
        String issuerName,
        String credentialName,
        String credentialFormat,
        List<WebviewClaimField> claims,
        String error) {
}
