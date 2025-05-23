package org.omnione.did.base.config.gson;

import com.google.gson.*;
import org.omnione.did.base.datamodel.enums.VerifyAuthType;

import java.lang.reflect.Type;

/**
 * Description...
 */
public class VerifyAuthTypeAdapter implements JsonSerializer<VerifyAuthType>, JsonDeserializer<VerifyAuthType> {

    @Override
    public JsonElement serialize(VerifyAuthType src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getValue());
    }

    @Override
    public VerifyAuthType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        int value = json.getAsInt();
        for (VerifyAuthType type : VerifyAuthType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new JsonParseException("Unknown VerifyAuthType value: " + value);
    }
}