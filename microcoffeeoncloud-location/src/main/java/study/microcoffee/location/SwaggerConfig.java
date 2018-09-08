package study.microcoffee.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuration class of Swagger.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket locationApi() {
        return new Docket(DocumentationType.SWAGGER_2) //
            .select() //
            .apis(RequestHandlerSelectors.basePackage("study.microcoffee")) //
            .paths(PathSelectors.any()) //
            .build() //
            .apiInfo(locationApiInfo()) //
            .useDefaultResponseMessages(false) //
            .globalResponseMessage(RequestMethod.GET, locationResponseMessages());
    }

    private ApiInfo locationApiInfo() {
        return new ApiInfo("Location API", "Finds the coffee shop closest to the current position.", "1.0", "",
            new Contact("Dagbjørn Nogva", "https://github.com/dagbjorn", ""), "", "", new ArrayList<>()) {
        };
    }

    private List<ResponseMessage> locationResponseMessages() {
        return Arrays.asList( //
            new ResponseMessageBuilder() //
                .code(200) //
                .message("OK") //
                .responseModel(new ModelRef("string")) //
                .build(),
            new ResponseMessageBuilder() //
                .code(204) //
                .message("No coffee shop found") //
                .build());
    }
}
