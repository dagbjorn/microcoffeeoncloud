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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import study.microcoffee.order.SwaggerConfig;

/**
 * API documentation of operation to get an order.
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
@Operation(operationId = "getOrder", //
    tags = { SwaggerConfig.ORDER_TAG }, //
    summary = "Gets an order.", //
    parameters = { //
        @Parameter(in = ParameterIn.PATH, name = "coffeeShopId", description = "Coffee shop ID.", example = "1"), //
        @Parameter(in = ParameterIn.PATH, name = "orderId", description = "Order ID.", example = "61c8229a9501224146c670ce"), //
        @Parameter(ref = SwaggerConfig.CORRELATION_ID_HEADER) }, //
    responses = { //
        @ApiResponse(responseCode = "200", description = "Order found and returned in the JSON-formatted HTTP response body."), //
        @ApiResponse(responseCode = "204", description = "Requested order ID is not found.", content = @Content) })
public @interface GetOrderSwagger {
}
