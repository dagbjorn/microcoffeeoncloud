package study.microcoffee.order.api.order;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import study.microcoffee.order.SwaggerConfig;

/**
 * API documentation of operation to create an order.
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
@Operation(operationId = "createOrder", //
    tags = { SwaggerConfig.ORDER_TAG }, //
    summary = "Creates an order.", //
    parameters = { //
        @Parameter(in = ParameterIn.PATH, name = "coffeeShopId", description = "Coffee shop ID.", schema = @Schema(type = "string", example = "1")), //
        @Parameter(ref = SwaggerConfig.CORRELATION_ID_HEADER) }, //
    requestBody = @RequestBody(description = "JSON-formatted coffee order.", //
        content = @Content(examples = { @ExampleObject(name = "Coffee order with options", //
            value = "{\"coffeeShopId\":1,\"drinker\":\"Dagbjørn\",\"size\":\"Small\",\"type\":{\"name\":\"Americano\",\"family\":\"Coffee\"},\"selectedOptions\":[\"soy\",\"decaf\"]}") })), //
    responses = { //
        @ApiResponse(responseCode = "201", description = "New order created."), //
        @ApiResponse(responseCode = "402", description = "Too low credit rating to accept order. Payment required!", content = @Content) })
public @interface CreateOrderSwagger {
}
