package org.omnione.did.base.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.omnione.did.base.config.gson.InstantTypeAdapter;
import org.omnione.did.base.config.gson.LocalDateTimeTypeAdapter;
import org.omnione.did.base.config.gson.VerifyAuthTypeAdapter;
import org.omnione.did.base.datamodel.enums.VerifyAuthType;
import org.omnione.did.issuer.v1.admin.controller.SessionController;
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
                .setFieldNamingStrategy(new com.google.gson.FieldNamingStrategy() {
                    @Override
                    public String translateName(java.lang.reflect.Field f) {
                        com.fasterxml.jackson.annotation.JsonProperty jsonProperty = f.getAnnotation(com.fasterxml.jackson.annotation.JsonProperty.class);
                        if (jsonProperty != null && !jsonProperty.value().isEmpty() && !com.fasterxml.jackson.annotation.JsonProperty.USE_DEFAULT_NAME.equals(jsonProperty.value())) {
                            return jsonProperty.value();
                        }
                        return f.getName();
                    }
                })
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                .registerTypeAdapter(VerifyAuthType.class, new VerifyAuthTypeAdapter())
                .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeTypeAdapter())
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