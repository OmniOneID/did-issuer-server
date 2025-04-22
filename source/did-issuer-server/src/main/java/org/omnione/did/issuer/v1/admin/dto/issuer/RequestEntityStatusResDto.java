package org.omnione.did.issuer.v1.admin.dto.issuer;

import lombok.*;
import org.omnione.did.issuer.v1.admin.constant.EntityStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RequestEntityStatusResDto {
    private EntityStatus status;
}
