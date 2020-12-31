package study.microcoffee.order.api.order;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.github.resilience4j.retry.RetryRegistry;
import study.microcoffee.order.api.order.model.OrderModel;
import study.microcoffee.order.consumer.creditrating.CreditRating;
import study.microcoffee.order.domain.DrinkType;
import study.microcoffee.order.test.DiscoveryTestConfig;

/**
 * Integration tests of {@link OrderController}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMetrics
@TestPropertySource("/application-test.properties")
@Import(DiscoveryTestConfig.class)
@ActiveProfiles("itest")
@Profile("itest")
public class OrderControllerIT {

    private static final String POST_SERVICE_PATH = "/api/coffeeshop/{coffeeShopId}/order";
    private static final String GET_SERVICE_PATH = "/api/coffeeshop/{coffeeShopId}/order/{orderId}";

    // Credit rating port
    private static final int CREDIT_RATING_PORT = 8083;

    private static final int COFFEE_SHOP_ID = 10;

    private static final String PROMETHEUS_METRIC_FAILED_WITH_RETRY = getMetricName("resilience4j_retry_calls_total", "order",
        "failed_with_retry", "creditRating");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    protected RetryRegistry retryRegistry;

    private static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(CREDIT_RATING_PORT));

    @BeforeAll
    public static void beforeAll() {
        wireMockServer.start();

        // Client-side configuration (default port is 8080)
        WireMock.configureFor(CREDIT_RATING_PORT);
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    @AfterEach
    public void afterEach() {
        wireMockServer.resetAll();
    }

    @Test
    public void saveOrderAndReadBackShouldReturnSavedOrder() throws Exception {
        // WireMock stubbing of CreditRating API
        final String creditRatingResponse = objectMapper.writeValueAsString(new CreditRating(50));

        stubFor(get(urlPathMatching("/api/coffeeshop/creditrating/(.+)")) //
            .willReturn(aResponse() //
                .withStatus(HttpStatus.OK.value()) //
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE) //
                .withBody(creditRatingResponse)));

        OrderModel newOrder = OrderModel.builder() //
            .type(new DrinkType("Latte", "Coffee")) //
            .size("Small") //
            .drinker("Dagbjørn") //
            .selectedOptions(new String[] { "skimmed milk" }) //
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Forwarded-Host", "forwardedhost.no");
        HttpEntity<OrderModel> requestEntity = new HttpEntity<>(newOrder, headers);

        ResponseEntity<OrderModel> response = restTemplate.exchange(POST_SERVICE_PATH, HttpMethod.POST, requestEntity,
            OrderModel.class, COFFEE_SHOP_ID);

        OrderModel savedOrder = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).asString().startsWith(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getHeaders().getLocation().toString()).contains("forwardedhost.no");
        assertThat(response.getHeaders().getLocation().toString()).endsWith(savedOrder.getId());
        assertThat(savedOrder.getType().getName()).isEqualTo("Latte");
        assertThat(savedOrder.getDrinker()).isEqualTo("Dagbjørn");

        response = restTemplate.getForEntity(GET_SERVICE_PATH, OrderModel.class, COFFEE_SHOP_ID, savedOrder.getId());

        OrderModel readBackOrder = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).asString().startsWith(MediaType.APPLICATION_JSON_VALUE);
        assertThat(readBackOrder).hasToString(savedOrder.toString());
    }

    @Test
    @EnabledIf("isResilience4jConsumer")
    public void saveOrderWhenCreditRatingNotAvailableShouldFailAfterRetry() throws Exception {
        float currentFailedWithRetryCount = getMetricValue(PROMETHEUS_METRIC_FAILED_WITH_RETRY);

        stubFor(get(urlPathMatching("/api/coffeeshop/creditrating/(.+)")) //
            .willReturn(status(HttpStatus.SERVICE_UNAVAILABLE.value())));

        OrderModel newOrder = OrderModel.builder() //
            .type(new DrinkType("Latte", "Coffee")) //
            .size("Small") //
            .drinker("Dagbjørn") //
            .selectedOptions(new String[] { "skimmed milk" }) //
            .build();

        ResponseEntity<OrderModel> response = restTemplate.postForEntity(POST_SERVICE_PATH, newOrder, OrderModel.class,
            COFFEE_SHOP_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYMENT_REQUIRED);
        assertThat(getMetricValue(PROMETHEUS_METRIC_FAILED_WITH_RETRY)).isEqualTo(currentFailedWithRetryCount + 1);
    }

    @Test
    public void getOrderWhenNoOrderShouldReturnNoContent() throws Exception {
        String orderId = "1111111111111111";

        ResponseEntity<OrderModel> response = restTemplate.getForEntity(GET_SERVICE_PATH, OrderModel.class, COFFEE_SHOP_ID,
            orderId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private static String getMetricName(String metric, String application, String kind, String backend) {
        return metric + "{application=\"" + application + "\",kind=\"" + kind + "\",name=\"" + backend + "\",} ";
    }

    private float getMetricValue(String key) {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/prometheus", String.class);

        String value = response.getBody().lines().filter(line -> line.startsWith(key)).findFirst().get().split("\\s")[1];
        return Float.valueOf(value);
    }

    /* Used by @EnableIf annotation. */
    @SuppressWarnings("unused")
    private boolean isResilience4jConsumer() {
        return OrderController.CREDIT_RATING_CONSUMER.equals(OrderController.RESILIENCE4J_CONSUMER)
            || OrderController.CREDIT_RATING_CONSUMER.equals(OrderController.RESILIENCE4J_WEB_CLIENT_CONSUMER);
    }
}
