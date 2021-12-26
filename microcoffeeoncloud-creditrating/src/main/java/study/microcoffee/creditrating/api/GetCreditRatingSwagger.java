package study.microcoffee.creditrating.api;

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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import study.microcoffee.creditrating.SwaggerConfig;

/**
 * API documentation of operation to get credit rating of a customer.
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
@Operation(operationId = "getCreditRating", //
    tags = { SwaggerConfig.CREDIT_RATING_TAG }, //
    summary = "Gets the credit rating of a customer.", //
    security = { @SecurityRequirement(name = SwaggerConfig.BEARER_TOKEN_AUTH) }, //
    parameters = { //
        @Parameter(in = ParameterIn.PATH, name = "customerId", description = "Customer ID.", example = "12345"), //
        @Parameter(ref = SwaggerConfig.CORRELATION_ID_HEADER) }, //
    responses = { //
        @ApiResponse(responseCode = "200", description = "Returns the credit rating."), //
        @ApiResponse(responseCode = "401", description = "Authentication error.", content = @Content),
        @ApiResponse(responseCode = "500", description = "Internal server error.", content = @Content) })
public @interface GetCreditRatingSwagger {
}
