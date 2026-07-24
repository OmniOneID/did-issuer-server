package org.omnione.did.issuer.v1.admin.dto.oid4vci;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListProviderRegistrationResultDto {
    private Long id;
    private String status;
}
