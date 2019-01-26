package study.microcoffee.location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.RequestMethod;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
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
            .paths(PathSelectors.ant("/api/**")) //
            .build() //
            .apiInfo(locationApiInfo()) //
            .useDefaultResponseMessages(false) //
            .globalResponseMessage(RequestMethod.GET, locationResponseMessages());
    }

    private ApiInfo locationApiInfo() {
        return new ApiInfoBuilder() //
            .title("Location API") //
            .description("Finds the coffee shop closest to the current position.") //
            .version("1.0") //
            .contact(new Contact("Dagbjørn Nogva", "https://github.com/dagbjorn", "")) //
            .build();
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

    /**
     * Configures Swagger resources to use. Adds a custom spec in addition to the built-in default spec.
     */
    @Primary
    @Bean
    public SwaggerResourcesProvider swaggerResourcesProvider(InMemorySwaggerResourcesProvider defaultResourcesProvider) {
        return new SwaggerResourcesProvider() {

            @Override
            public List<SwaggerResource> get() {

                List<SwaggerResource> resources = new ArrayList<>();
                resources.add(swaggerResource("Location API [custom spec]", "/swagger/location-apispec.yml", "2.0"));
                resources.addAll(defaultResourcesProvider.get());
                return resources;
            }

            private SwaggerResource swaggerResource(String name, String location, String version) {
                SwaggerResource resource = new SwaggerResource();
                resource.setName(name);
                resource.setLocation(location);
                resource.setSwaggerVersion(version);
                return resource;
            }
        };
    }
}
