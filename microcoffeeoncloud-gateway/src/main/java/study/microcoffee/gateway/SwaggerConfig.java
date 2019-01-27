package study.microcoffee.gateway;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuration class of Swagger.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Configures Swagger resources to use.
     */
    @Primary
    @Bean
    public SwaggerResourcesProvider swaggerResourcesProvider() {
        return new SwaggerResourcesProvider() {

            @Override
            public List<SwaggerResource> get() {

                List<SwaggerResource> resources = new ArrayList<>();
                resources.add(swaggerResource("Credit Rating API", "/apidocs/creditrating/v2/api-docs", "2.0"));
                resources
                    .add(swaggerResource("Location API [custom spec]", "/apidocs/location/swagger/location-apispec.yml", "2.0"));
                resources.add(swaggerResource("Menu/Order API", "/apidocs/order/v2/api-docs", "2.0"));
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