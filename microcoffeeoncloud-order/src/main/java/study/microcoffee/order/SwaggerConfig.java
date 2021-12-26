package study.microcoffee.order;

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
import io.swagger.v3.oas.models.tags.Tag;

/**
 * Configuration class of Swagger.
 */
@Configuration
public class SwaggerConfig {

    public static final String MENU_TAG = "Menu";
    public static final String ORDER_TAG = "Order";

    public static final String CORRELATION_ID_HEADER = "Correlation-Id";

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI() //
            .info(new Info() //
                .title("Menu/Order API") //
                .description("API for use by customers to read the menu and order drinks.") //
                .version("1.0") //
                .contact(new Contact() //
                    .name("Dagbjørn Nogva") //
                    .url("https://github.com/dagbjorn")))
            .components(new Components() //
                .addParameters(CORRELATION_ID_HEADER, //
                    new Parameter() //
                        .in("header") //
                        .name(CORRELATION_ID_HEADER) //
                        .description("Correlation ID primarily used as cross-reference in logs.") //
                        .example(UUID.randomUUID().toString()) //
                        .schema(new StringSchema()))) //
            .tags(List.of( //
                new Tag() //
                    .name(MENU_TAG) //
                    .description("API to get the coffee menu."), //
                new Tag() //
                    .name(ORDER_TAG) //
                    .description("API for handling coffee orders.")));
    }
}
