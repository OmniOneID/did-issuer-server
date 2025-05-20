package org.omnione.did.base.datamodel.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Description...
 */
@Getter
public enum InitiateType {
    @SerializedName("user_init")
    USER_INIT("user_init"),

    @SerializedName("issuer_init")
    ISSUER_INIT("issuer_init");


    @JsonValue
    private String type;

    InitiateType(String type) {
        this.type = type;
    }
}
