package org.omnione.did.issuer.v1.admin.dto.user;

import lombok.*;

/**
 * Request DTO for creating or updating user information in the Admin Console.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreateUserInfoFromDemoReqDto {
    private String did;
    private String pii;
    private String vcSchemaId;
    private String userInfo;
}
