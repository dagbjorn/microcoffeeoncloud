package study.microcoffee.order.consumer.creditrating;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import study.microcoffee.order.exception.ServiceCallFailedException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Unit tests of {@link BasicRestClientCreditRatingConsumer}.
 */
class BasicRestClientCreditRatingConsumerTest {

    private static final String CREDITRATING_SERVICE_URL = "http://dummy";

    private MockRestServiceServer server;

    private RestClient.Builder restClientBuilder = RestClient.builder();

    private JsonMapper jsonMapper = JsonMapper.builder().build();

    private CreditRatingConsumer creditRatingConsumer;

    @BeforeEach
    void setUp() {
        server = MockRestServiceServer.bindTo(restClientBuilder).build();

        creditRatingConsumer = new BasicRestClientCreditRatingConsumer(restClientBuilder, CREDITRATING_SERVICE_URL);
    }

    @Test
    void getCreateRatingWhenHttpStatus200ShouldReturnRating() {
        final String customerId = "john@company.com";
        final String expectedContent = jsonMapper.writeValueAsString(new CreditRating(50));

        server.expect(once(), requestTo(buildServiceUrl(customerId))) //
            .andExpect(method(HttpMethod.GET)) //
            .andRespond(withSuccess(expectedContent, MediaType.APPLICATION_JSON));

        int creditRating = creditRatingConsumer.getCreditRating(customerId);

        assertThat(creditRating).isEqualTo(50);
    }

    @Test
    void getCreateRatingWhenHttpStatus204ShouldThrowServiceCallFailed() {
        final String customerId = "john@company.com";

        server.expect(once(), requestTo(buildServiceUrl(customerId))) //
            .andExpect(method(HttpMethod.GET)) //
            .andRespond(withNoContent());

        Assertions.assertThrows(ServiceCallFailedException.class, () -> {
            creditRatingConsumer.getCreditRating(customerId);
        });
    }

    @Test
    void getCreateRatingWhenHttpStatus500ShouldThrowServiceCallFailed() {
        final String customerId = "john@company.com";

        server.expect(once(), requestTo(buildServiceUrl(customerId))) //
            .andExpect(method(HttpMethod.GET)) //
            .andRespond(withServerError());

        Assertions.assertThrows(ServiceCallFailedException.class, () -> {
            creditRatingConsumer.getCreditRating(customerId);
        });
    }

    private String buildServiceUrl(String customerId) {
        UriComponents serviceUrl = UriComponentsBuilder.fromUriString(CREDITRATING_SERVICE_URL) //
            .path("/api/coffeeshop/creditrating") //
            .pathSegment(UriUtils.encode(customerId, StandardCharsets.UTF_8)) //
            .build();

        return serviceUrl.toUriString();
    }
}
