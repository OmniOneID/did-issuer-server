package org.omnione.did.issuer.v1.agent.service.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Sample data provider for credential issuance tests.
 */
@Getter
public class SampleData {

    private static final String SAMPLE_DATA_FILE = "sample-data.json";
    private static final String DEFAULT_PII = "default";

    private final ObjectMapper objectMapper;
    private final Map<String, Map<String, Object>> sampleDataSet;

    public SampleData() {
        this.objectMapper = new ObjectMapper();
        this.sampleDataSet = loadSampleDataSet();
    }

    public Map<String, String> getSampleDataByPii(String pii) {
        Map<String, Object> sampleData = sampleDataSet.getOrDefault(pii, sampleDataSet.get(DEFAULT_PII));

        if (sampleData == null) {
            throw new IllegalStateException("Default sample data is missing.");
        }

        return sampleData.entrySet()
                .stream()
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> writeJsonAsString(entry.getValue())
                ));
    }

    private Map<String, Map<String, Object>> loadSampleDataSet() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(SAMPLE_DATA_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Sample data file not found: " + SAMPLE_DATA_FILE);
            }

            return objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load sample data file: " + SAMPLE_DATA_FILE, e);
        }
    }

    private String writeJsonAsString(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to convert sample data to JSON string.", e);
        }
    }
}