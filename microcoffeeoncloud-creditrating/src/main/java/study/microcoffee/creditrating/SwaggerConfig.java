package study.microcoffee.creditrating;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * Configuration class of Swagger.
 * <p>
 * See <code>application.properties</code> for more configuration.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI() //
            .info(new Info() //
                .title("Credit Rating API") //
                .description("API to get the credit rating of customers.") //
                .version("1.0") //
                .contact(new Contact() //
                    .name("Dagbjørn Nogva") //
                    .url("https://github.com/dagbjorn")));
    }
}
