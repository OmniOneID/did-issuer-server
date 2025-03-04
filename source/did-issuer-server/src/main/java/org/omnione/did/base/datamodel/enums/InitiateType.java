package org.omnione.did.base.datamodel.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Description...
 */
@Getter
public enum InitiateType {
    USER_INIT("user_init"),
    ISSUER_INIT("issuer_init");

    @JsonValue
    private String type;

    InitiateType(String type) {
        this.type = type;
    }
}
