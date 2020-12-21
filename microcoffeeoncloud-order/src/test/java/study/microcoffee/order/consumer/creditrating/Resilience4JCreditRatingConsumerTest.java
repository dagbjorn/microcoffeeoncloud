package study.microcoffee.order.consumer.creditrating;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import study.microcoffee.order.exception.ServiceCallFailedException;

/**
 * Unit tests of {@link Resilience4JCreditRatingConsumer}.
 */
public class Resilience4JCreditRatingConsumerTest {

    private static final String CREDITRATING_SERVICE_URL = "http://dummy";

    private MockRestServiceServer server;

    private RestTemplate restTemplate = new RestTemplate();

    private ObjectMapper objectMapper = new ObjectMapper();

    private CreditRatingConsumer creditRatingConsumer;

    @BeforeEach
    public void setUp() {
        server = MockRestServiceServer.bindTo(restTemplate).build();

        creditRatingConsumer = new Resilience4JCreditRatingConsumer(restTemplate, CREDITRATING_SERVICE_URL);
    }

    @Test
    public void getCreateRatingWhenHttpStatus200ShouldReturnRating() throws Exception {
        final String customerId = "john@company.com";
        final String expectedContent = objectMapper.writeValueAsString(new CreditRating(50));

        server.expect(once(), requestTo(buildServiceUrl(customerId))) //
            .andExpect(method(HttpMethod.GET)) //
            .andRespond(withSuccess(expectedContent, MediaType.APPLICATION_JSON));

        int creditRating = creditRatingConsumer.getCreditRating(customerId);

        assertThat(creditRating).isEqualTo(50);
    }

    @Test
    public void getCreateRatingWhenHttpStatus500ShouldThrowServiceCallFailed() throws Exception {
        Assertions.assertThrows(ServiceCallFailedException.class, () -> {
            final String customerId = "john@company.com";

            server.expect(once(), requestTo(buildServiceUrl(customerId))) //
                .andExpect(method(HttpMethod.GET)) //
                .andRespond(withServerError());

            creditRatingConsumer.getCreditRating(customerId);
        });
    }

    private String buildServiceUrl(String customerId) throws UnsupportedEncodingException {
        UriComponents serviceUrl = UriComponentsBuilder.fromHttpUrl(CREDITRATING_SERVICE_URL) //
            .path("/api/coffeeshop/creditrating") //
            .pathSegment(UriUtils.encodePathSegment(customerId, StandardCharsets.UTF_8.name())) //
            .build();
        return serviceUrl.toUriString();
    }
}
