package study.microcoffee.order.consumer.creditrating;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import study.microcoffee.order.exception.ServiceCallFailedException;

/**
 * Unit tests of {@link HystrixCreditRatingConsumer}.
 */
@RunWith(SpringRunner.class)
@RestClientTest(HystrixCreditRatingConsumer.class)
@TestPropertySource(properties = { "app.creditrating.url=http://dummy" })
public class HystrixCreditRatingConsumerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

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

    @Test(expected = ServiceCallFailedException.class)
    public void getCreateRatingWhenHttpStatus500ShouldThrowServiceCallFailed() throws Exception {
        final String customerId = "john@company.com";

        server.expect(once(), requestTo(buildServicePath(customerId))) //
            .andExpect(method(HttpMethod.GET)) //
            .andRespond(withServerError());

        creditRatingConsumer.getCreditRating(customerId);
    }

    private String buildServicePath(String customerId) throws UnsupportedEncodingException {
        return "/coffeeshop/creditrating/" + UriUtils.encodePathSegment(customerId, StandardCharsets.UTF_8.name());
    }
}
