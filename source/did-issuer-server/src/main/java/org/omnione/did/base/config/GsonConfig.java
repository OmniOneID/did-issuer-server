package org.omnione.did.base.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.omnione.did.base.config.gson.InstantTypeAdapter;
import org.omnione.did.base.config.gson.VerifyAuthTypeAdapter;
import org.omnione.did.base.datamodel.enums.VerifyAuthType;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.time.Instant;

/**
 * Description...
 */
@Configuration
public class GsonConfig {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .registerTypeAdapter(VerifyAuthType.class, new VerifyAuthTypeAdapter())

                .setPrettyPrinting()
                .create();
    }

    @Bean
    public HttpMessageConverters customConverters() {
        GsonHttpMessageConverter gsonConverter = new GsonHttpMessageConverter();
        gsonConverter.setGson(gson());
        return new HttpMessageConverters(gsonConverter);
    }
}