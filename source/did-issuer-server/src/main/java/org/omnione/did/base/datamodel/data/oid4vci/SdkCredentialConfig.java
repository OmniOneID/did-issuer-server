package org.omnione.did.base.datamodel.data.oid4vci;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SdkCredentialConfig {
    private String id;
    private String format;
    private List<String> identifiers;
    private String metadataJson;
}
