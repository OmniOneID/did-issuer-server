package org.omnione.did.base.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Description...
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "kyc")
public class KycProperty {
    private String url;
}
