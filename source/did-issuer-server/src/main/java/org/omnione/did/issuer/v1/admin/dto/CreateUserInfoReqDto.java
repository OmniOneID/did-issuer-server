package org.omnione.did.issuer.v1.admin.dto;

import lombok.*;

/**
 * Description...
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CreateUserInfoReqDto {
    private Long id;
    private String did;
    private String firstName;
    private String lastName;
    private String pii;
    private Long vcSchemaId;
    private String userInfo;
}
