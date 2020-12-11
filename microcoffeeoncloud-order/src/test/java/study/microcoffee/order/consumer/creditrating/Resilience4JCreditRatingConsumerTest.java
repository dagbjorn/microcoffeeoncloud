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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import study.microcoffee.order.exception.ServiceCallFailedException;

/**
 * Unit tests of {@link Resilience4JCreditRatingConsumer}.
 */
@RestClientTest(Resilience4JCreditRatingConsumer.class)
@TestPropertySource("/application-test.properties")
public class Resilience4JCreditRatingConsumerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private CreditRatingConsumer creditRatingConsumer;

    @Test
    public void getCreateRatingWhenHttpStatus200ShouldReturnRating() throws Exception {
        final String customerId = "john@company.com";
        final String expectedContent = objectMapper.writeValueAsString(new CreditRating(50));

        server.expect(once(), requestTo(buildServicePath(customerId))) //
            .andExpect(method(HttpMethod.GET)) //
            .andRespond(withSuccess(expectedContent, MediaType.APPLICATION_JSON));

        int creditRating = creditRatingConsumer.getCreditRating(customerId);

        assertThat(creditRating).isEqualTo(50);
    }

    @Test
    public void getCreateRatingWhenHttpStatus500ShouldThrowServiceCallFailed() throws Exception {
        Assertions.assertThrows(ServiceCallFailedException.class, () -> {
            final String customerId = "john@company.com";

            server.expect(once(), requestTo(buildServicePath(customerId))) //
                .andExpect(method(HttpMethod.GET)) //
                .andRespond(withServerError());

            creditRatingConsumer.getCreditRating(customerId);
        });
    }

    private String buildServicePath(String customerId) throws UnsupportedEncodingException {
        return "/api/coffeeshop/creditrating/" + UriUtils.encodePathSegment(customerId, StandardCharsets.UTF_8.name());
    }
}
