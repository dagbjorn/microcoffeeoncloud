package study.microcoffee.order.consumer.creditrating;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import study.microcoffee.order.exception.ServiceCallFailedException;

/**
 * Unit tests of {@link BasicWebClientCreditRatingConsumer}.
 */
public class BasicWebClientCreditRatingConsumerTest {

    private MockWebServer server;

    private WebClient webClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    private CreditRatingConsumer creditRatingConsumer;

    @BeforeEach
    public void startServer() {
        server = new MockWebServer();

        webClient = WebClient.builder() //
            .clientConnector(new JettyClientHttpConnector()) //
            .build();

        creditRatingConsumer = new BasicWebClientCreditRatingConsumer(webClient, server.url("/").toString());
    }

    @AfterEach
    void shutdown() throws IOException {
        server.shutdown();
    }

    @Test
    public void getCreateRatingWhenHttpStatus200ShouldReturnRating() throws Exception {
        final String customerId = "john@company.com";
        final String expectedContent = objectMapper.writeValueAsString(new CreditRating(50));

        server.enqueue(new MockResponse() //
            .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) //
            .setResponseCode(HttpStatus.OK.value()) //
            .setBody(expectedContent));

        int creditRating = creditRatingConsumer.getCreditRating(customerId);

        assertThat(creditRating).isEqualTo(50);

        RecordedRequest request = server.takeRequest();
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET.name());
        assertThat(request.getPath()).isEqualTo(BasicWebClientCreditRatingConsumer.GET_CREDIT_RATING_RESOURCE
            .replace("{customerId}", URLEncoder.encode(customerId, StandardCharsets.UTF_8)));
    }

    @Test
    public void getCreateRatingWhenHttpStatus500ShouldThrowServiceCallFailed() throws Exception {
        final String customerId = "john@company.com";

        server.enqueue(new MockResponse() //
            .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));

        Assertions.assertThrows(ServiceCallFailedException.class, () -> {
            creditRatingConsumer.getCreditRating(customerId);
        });
    }

    @Test
    public void getCreateRatingWhenHttpStatus204ShouldThrowServiceCallFailed() throws Exception {
        final String customerId = "john@company.com";

        server.enqueue(new MockResponse() //
            .setResponseCode(HttpStatus.NO_CONTENT.value()));

        Assertions.assertThrows(ServiceCallFailedException.class, () -> {
            creditRatingConsumer.getCreditRating(customerId);
        });
    }
}
