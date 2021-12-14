package study.microcoffee.order;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * Configuration class of Swagger.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI() //
            .info(new Info() //
                .title("Menu/Order API") //
                .description("API for use by customers to read the menu and order drinks.") //
                .version("1.0") //
                .contact(new Contact() //
                    .name("Dagbjørn Nogva") //
                    .url("https://github.com/dagbjorn")));
    }
}
