package study.microcoffee.order;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Configuration class of Swagger.
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket apiSwaggerConfig() {
        return new Docket(DocumentationType.SWAGGER_2) //
            .select() //
            .paths(PathSelectors.ant("/api/**")) //
            .build() //
            .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder() //
            .title("Menu/Order API") //
            .description("API for use by customers to read the menu and order drinks.") //
            .version("1.0") //
            .contact(new Contact("Dagbjørn Nogva", "https://github.com/dagbjorn", "")) //
            .build();
    }
}
