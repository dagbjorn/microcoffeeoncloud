package study.microcoffee.creditrating;

import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;

/**
 * Configuration class of Swagger.
 * <p>
 * See <code>application.properties</code> for more configuration.
 */
@Configuration
public class SwaggerConfig {

    public static final String CREDIT_RATING_TAG = "Credit Rating";

    public static final String CORRELATION_ID_HEADER = "Correlation-Id";
    public static final String BEARER_TOKEN_AUTH = "BearerToken";

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI() //
            .info(new Info() //
                .title("Credit Rating API") //
                .description("API to get the credit rating of customers.") //
                .version("1.0") //
                .contact(new Contact() //
                    .name("Dagbjørn Nogva") //
                    .url("https://github.com/dagbjorn"))) //
            .components(new Components() //
                .addSecuritySchemes(BEARER_TOKEN_AUTH, //
                    new SecurityScheme() //
                        .type(SecurityScheme.Type.HTTP) //
                        .scheme("bearer") //
                        .bearerFormat("JWT") //
                        .description("Copy&paste an access token issued by Keycloak.")) //
                .addParameters(CORRELATION_ID_HEADER, //
                    new Parameter() //
                        .in("header") //
                        .name(CORRELATION_ID_HEADER) //
                        .description("Correlation ID primarily used as cross-reference in logs.") //
                        .schema(new StringSchema().example(UUID.randomUUID().toString())))) //
            .tags(List.of(new Tag() //
                .name(CREDIT_RATING_TAG) //
                .description("API to get the credit rating of a customer.")));
    }
}
