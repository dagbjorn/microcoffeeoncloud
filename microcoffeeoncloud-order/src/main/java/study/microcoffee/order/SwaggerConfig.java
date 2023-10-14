package study.microcoffee.order;

import java.util.List;
import java.util.UUID;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.servers.Server;

/**
 * Configuration class of Swagger.
 */
@Configuration
public class SwaggerConfig {

    public static final String MENU_TAG = "Menu";
    public static final String ORDER_TAG = "Order";

    public static final String MENU_DESCRIPTION = "API to get the coffee menu.";
    public static final String ORDER_DESCRIPTION = "API for handling coffee orders.";

    public static final String CORRELATION_ID_HEADER = "Correlation-Id";

    @Value("${app.springdoc.serverListOverrideUrl:#{null}}")
    private String serverOverrideUrl;

    @Bean
    public GroupedOpenApi menuApiGroup() {
        return GroupedOpenApi.builder() //
            .group("menu") //
            .packagesToScan("study.microcoffee.order.api.menu") //
            .addOpenApiCustomizer(openApi -> {
                openApi.getInfo() //
                    .title("Menu API") //
                    .description("API for use by customers to get the coffee menu.") //
                    .version("1.0") //
                    .contact(apiContact());
                openApi.getComponents() //
                    .addParameters(CORRELATION_ID_HEADER, correlationIdHeader()); //

                if (serverOverrideUrl != null) {
                    openApi.servers(List.of(new Server().url(serverOverrideUrl)));
                }
            }).build();
    }

    @Bean
    public GroupedOpenApi orderApiGroup() {
        return GroupedOpenApi.builder() //
            .group("order") //
            .packagesToScan("study.microcoffee.order.api.order") //
            .addOpenApiCustomizer(openApi -> {
                openApi.getInfo() //
                    .title("Order API") //
                    .description("API for use by customers to order drinks.") //
                    .version("1.0") //
                    .contact(apiContact());
                openApi.getComponents() //
                    .addParameters(CORRELATION_ID_HEADER, correlationIdHeader()); //

                if (serverOverrideUrl != null) {
                    openApi.servers(List.of(new Server().url(serverOverrideUrl)));
                }
            }).build();
    }

    private Contact apiContact() {
        return new Contact() //
            .name("Dagbjørn Nogva") //
            .url("https://github.com/dagbjorn");
    }

    private Parameter correlationIdHeader() {
        return new Parameter() //
            .in("header") //
            .name(CORRELATION_ID_HEADER) //
            .description("Correlation ID primarily used as cross-reference in logs.") //
            .schema(new StringSchema().example(UUID.randomUUID().toString()));
    }
}
