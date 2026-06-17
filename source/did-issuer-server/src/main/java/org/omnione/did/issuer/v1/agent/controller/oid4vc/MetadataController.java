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

package org.omnione.did.issuer.v1.agent.controller.oid4vc;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.oid4vc.oid4vci.property.IssuerProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MetadataController {

    private final IssuerProperties issuerProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Path getMetadataDirectory() {
        String dataDir = issuerProperties.getDataDir();
        if (dataDir == null || dataDir.isBlank()) {
            throw new IllegalStateException("issuer.data-dir is not configured");
        }
        
        // Use metadata directory within dataDir
        Path metadataDir = Paths.get(dataDir, "metadata");
        
        // Ensure directory exists for metadata editor
        if (!Files.exists(metadataDir)) {
            try {
                Files.createDirectories(metadataDir);
            } catch (IOException e) {
                log.error("Failed to create metadata directory at: {}", metadataDir, e);
            }
        }
        return metadataDir;
    }

    @GetMapping("/metadata-page")
    public String metadataPage() {
        return "metadata-editor";
    }

    @GetMapping("/api/metadata/files")
    @ResponseBody
    public List<String> getMetadataFiles() {
        Path dir = getMetadataDirectory();
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return Collections.emptyList();
        }

        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.toLowerCase().endsWith(".json"))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to list metadata files", e);
            throw new RuntimeException("Failed to list files", e);
        }
    }

    @GetMapping("/api/metadata/file")
    @ResponseBody
    public ResponseEntity<Object> getFileContent(@RequestParam String filename) {
        if (filename == null || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new IllegalArgumentException("Invalid filename");
        }

        Path file = getMetadataDirectory().resolve(filename);
        if (!Files.exists(file)) {
            log.warn("Attempted to access non-existent file: {}", HtmlUtils.htmlEscape(filename));
            throw new IllegalArgumentException("File not found");
        }

        try {
            String content = Files.readString(file, StandardCharsets.UTF_8);
            
            // Parse raw JSON string to Object so that Jackson MessageConverter handles it safely
            Object jsonObject = objectMapper.readValue(content, Object.class);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-Content-Type-Options", "nosniff")
                    .body(jsonObject);
        } catch (IOException e) {
            log.error("Failed to read file: {}", HtmlUtils.htmlEscape(filename), e);
            throw new RuntimeException("Failed to read file");
        }
    }

    @PostMapping("/api/metadata/save")
    @ResponseBody
    public ResponseEntity<?> saveFileContent(@RequestBody Map<String, String> payload) {
        String filename = payload.get("filename");
        String content = payload.get("content");

        if (filename == null || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid filename"));
        }

        Path file = getMetadataDirectory().resolve(filename);
        try {
            // Backup functionality could be added here
            Files.writeString(file, content, StandardCharsets.UTF_8);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            log.error("Failed to write file: {}", HtmlUtils.htmlEscape(filename), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to save file"));
        }
    }
}
