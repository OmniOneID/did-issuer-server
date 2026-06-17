/*
 * Copyright 2024 OmniOne.
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

package org.omnione.did;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@EnableEncryptableProperties
@SpringBootApplication(exclude = {OAuth2ClientAutoConfiguration.class})
@ComponentScan(
        basePackages = "org.omnione.did",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "org\\.omnione\\.did\\.oid4vc\\.oid4vci\\.controller\\.WellKnownMetadataController"
        )
)
@ConfigurationPropertiesScan(basePackages = "org.omnione.did")
public class IssuerApplication {

    public static void main(String[] args) {
        SpringApplication.run(IssuerApplication.class, args);
    }

}
