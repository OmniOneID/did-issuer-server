package org.omnione.did.issuer.v1.admin.dto.user;


import lombok.*;

/**
 * DTO for serializing user information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SerializeUserInfoData {
    private String lastname;
    private String firstname;
}
