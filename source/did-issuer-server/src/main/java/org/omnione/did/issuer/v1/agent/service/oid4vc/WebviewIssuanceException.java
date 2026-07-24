/*
 * Copyright 2026 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 */
package org.omnione.did.issuer.v1.agent.service.oid4vc;

import org.springframework.http.HttpStatus;

public class WebviewIssuanceException extends RuntimeException {
    private final HttpStatus status;

    public WebviewIssuanceException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
