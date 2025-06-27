package org.omnione.did;

import org.omnione.did.base.config.JpaConfig;
import org.omnione.did.base.config.QuerydslConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A composed annotation used for integration testing of JPA-based repositories
 * in an in-memory database environment. This annotation is a specialized form
 * of {@code @DataJpaTest}, and it configures the database to use an H2 in-memory
 * JDBC connection with predefined settings for testing.
 * <p>
 * It automatically includes Spring Data JPA configurations and additional
 * configurations related to auditing and QueryDSL support by importing
 * {@code JpaConfig} and {@code QuerydslConfig} classes.
 * <p>
 * The annotation is primarily used to simplify the setup needed for testing
 * persistence layers by preconfiguring the necessary test environment.
 *
 * @author birariro
 */
@TestPropertySource(properties = {
        "spring.datasource.url = jdbc:h2:mem:test",
        "spring.datasource.driverClassName = org.h2.Driver",
        "spring.datasource.username = sa",
        "spring.datasource.password = ",
})
@DataJpaTest
@Import({JpaConfig.class, QuerydslConfig.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InMemoryDataJpaTest {
}
