package org.omnione.did.base.db.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.data.model.schema.SchemaClaims;

/**
 * Description...
 */
public class SchemaClaimConverter implements AttributeConverter<SchemaClaims, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(SchemaClaims schemaClaims) {
        try {
            return objectMapper.writeValueAsString(schemaClaims);
        } catch (JsonProcessingException e) {
            throw new OpenDidException(ErrorCode.JSON_SCHEMA_CLAIMS_SERIALIZE_FAILED);
        }
    }

    @Override
    public SchemaClaims convertToEntityAttribute(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, SchemaClaims.class);
        } catch (JsonProcessingException e) {
            throw new OpenDidException(ErrorCode.JSON_SCHEMA_CLAIMS_DESERIALIZE_FAILED);
        }
    }
}
