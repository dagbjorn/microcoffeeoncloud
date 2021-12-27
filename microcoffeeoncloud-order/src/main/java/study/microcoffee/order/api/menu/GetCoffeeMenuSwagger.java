package study.microcoffee.order.api.menu;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import study.microcoffee.order.SwaggerConfig;

/**
 * API documentation of operation to get coffee menu.
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
@Operation(operationId = "getCoffeeMenu", //
    tags = { SwaggerConfig.MENU_TAG }, //
    summary = "Gets the coffee menu.", //
    parameters = { //
        @Parameter(ref = SwaggerConfig.CORRELATION_ID_HEADER) }, //
    responses = { //
        @ApiResponse(responseCode = "200", description = "Returns the coffee menu.") })
public @interface GetCoffeeMenuSwagger {
}
