package org.omnione.did.base.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.File;
import java.io.IOException;

public class SerializationUtils {
    private static final ObjectMapper jsonMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    
    private static final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))
            .enable(SerializationFeature.INDENT_OUTPUT);

    public static <T> T readJson(String path, Class<T> clazz) throws IOException {
        return jsonMapper.readValue(new File(path), clazz);
    }

    public static void writeJson(String path, Object object) throws IOException {
        jsonMapper.writeValue(new File(path), object);
    }

    public static <T> T readYaml(String path, Class<T> clazz) throws IOException {
        return yamlMapper.readValue(new File(path), clazz);
    }

    public static void writeYaml(String path, Object object) throws IOException {
        yamlMapper.writeValue(new File(path), object);
    }
    
    public static String toJson(Object object) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
        return jsonMapper.readValue(json, clazz);
    }

    public static <T> T fromJson(String json, com.fasterxml.jackson.core.type.TypeReference<T> typeReference) throws JsonProcessingException {
        return jsonMapper.readValue(json, typeReference);
    }
}
