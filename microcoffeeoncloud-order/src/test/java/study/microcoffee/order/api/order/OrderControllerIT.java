package study.microcoffee.order.api.order;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.actuate.metrics.AutoConfigureMetrics;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.github.resilience4j.retry.RetryRegistry;
import study.microcoffee.jwttest.TestTokens;
import study.microcoffee.jwttest.oidcprovider.model.BearerToken;
import study.microcoffee.jwttest.oidcprovider.model.ProviderMetadata;
import study.microcoffee.order.api.order.model.OrderModel;
import study.microcoffee.order.consumer.creditrating.CreditRating;
import study.microcoffee.order.domain.DrinkType;

/**
 * Integration tests of {@link OrderController}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMetrics
@TestPropertySource("/application-test.properties")
@ActiveProfiles("itest")
@Profile("itest")
class OrderControllerIT {

    private static final int WIREMOCK_PORT = 9999;

    // Issuer must match value in application-test.properties.
    private static final String ISSUER = "http://localhost:" + WIREMOCK_PORT;
    private static final String WELLKNOWN_PATH = "/.well-known/openid-configuration";
    private static final String AUTHORIZATION_PATH = "/protocol/openid-connect/auth";
    private static final String TOKEN_PATH = "/protocol/openid-connect/token";
    private static final String JWKS_PATH = "/protocol/openid-connect/certs";

    private static final String POST_SERVICE_PATH = "/api/coffeeshop/{coffeeShopId}/order";
    private static final String GET_SERVICE_PATH = "/api/coffeeshop/{coffeeShopId}/order/{orderId}";

    private static final int COFFEE_SHOP_ID = 10;

    private static final String PROMETHEUS_METRIC_FAILED_WITH_RETRY = getMetricName("resilience4j_retry_calls_total", "order",
        "failed_with_retry", "creditRating");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    protected RetryRegistry retryRegistry;

    private static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(WIREMOCK_PORT));

    @BeforeAll
    static void beforeAll() throws Exception {
        wireMockServer.start();

        // Client-side configuration (default port is 8080)
        WireMock.configureFor(WIREMOCK_PORT);

        // Stub response of OIDC Discovery using WellKnown URL. Must be done before Spring context is created.
        stubWireMockWellKnownResponse();
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }

    @BeforeEach
    void beforeEach() throws Exception {
        stubWireMockTokenResponse();
    }

    @AfterEach
    void afterEach() {
        wireMockServer.resetAll();
    }

    @Test
    void createOrderAndReadBackShouldReturnSavedOrder() throws Exception {
        // WireMock stubbing of CreditRating API
        final String creditRatingResponse = objectMapper.writeValueAsString(new CreditRating(50));

        stubFor(get(urlPathMatching("/api/coffeeshop/creditrating/(.+)")) //
            .withHeader(HttpHeaders.AUTHORIZATION, containing("Bearer")) //
            .willReturn(aResponse() //
                .withStatus(HttpStatus.OK.value()) //
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) //
                .withBody(creditRatingResponse)));

        OrderModel newOrder = createOrder();

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
    @EnabledIf("isBasicConsumer")
    void createOrderWhenCreditRatingNotAvailableShouldFail() throws Exception {
        stubFor(get(urlPathMatching("/api/coffeeshop/creditrating/(.+)")) //
            .willReturn(status(HttpStatus.SERVICE_UNAVAILABLE.value())));

        ResponseEntity<OrderModel> response = restTemplate.postForEntity(POST_SERVICE_PATH, createOrder(), OrderModel.class,
            COFFEE_SHOP_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @EnabledIf("isResilience4jConsumer")
    void createOrderWhenCreditRatingNotAvailableShouldFailAfterRetry() throws Exception {
        float currentFailedWithRetryCount = getMetricValue(PROMETHEUS_METRIC_FAILED_WITH_RETRY);

        stubFor(get(urlPathMatching("/api/coffeeshop/creditrating/(.+)")) //
            .willReturn(status(HttpStatus.SERVICE_UNAVAILABLE.value())));

        ResponseEntity<OrderModel> response = restTemplate.postForEntity(POST_SERVICE_PATH, createOrder(), OrderModel.class,
            COFFEE_SHOP_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYMENT_REQUIRED);
        assertThat(getMetricValue(PROMETHEUS_METRIC_FAILED_WITH_RETRY)).isEqualTo(currentFailedWithRetryCount + 1);
    }

    @Test
    void getOrderWhenNoOrderShouldReturnNoContent() throws Exception {
        String orderId = "1111111111111111";

        ResponseEntity<OrderModel> response = restTemplate.getForEntity(GET_SERVICE_PATH, OrderModel.class, COFFEE_SHOP_ID,
            orderId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private OrderModel createOrder() {
        return OrderModel.builder() //
            .type(new DrinkType("Latte", "Coffee")) //
            .size("Small") //
            .drinker("Dagbjørn") //
            .selectedOptions(new String[] { "skimmed milk" }) //
            .build();
    }

    private static String getMetricName(String metric, String application, String kind, String backend) {
        return metric + "{application=\"" + application + "\",kind=\"" + kind + "\",name=\"" + backend + "\",} ";
    }

    private float getMetricValue(String key) {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/prometheus", String.class);

        String value = response.getBody().lines().filter(line -> line.startsWith(key)).findFirst().get().split("\\s")[1];
        return Float.valueOf(value);
    }

    //
    // Used by @EnableIf annotations
    //

    @SuppressWarnings("unused")
    private boolean isBasicConsumer() {
        return OrderController.CREDIT_RATING_CONSUMER.equals(OrderController.BASIC_CONSUMER)
            || OrderController.CREDIT_RATING_CONSUMER.equals(OrderController.BASIC_WEB_CLIENT_CONSUMER);
    }

    @SuppressWarnings("unused")
    private boolean isResilience4jConsumer() {
        return OrderController.CREDIT_RATING_CONSUMER.equals(OrderController.RESILIENCE4J_CONSUMER)
            || OrderController.CREDIT_RATING_CONSUMER.equals(OrderController.RESILIENCE4J_WEB_CLIENT_CONSUMER);
    }

    /**
     * Static stubbing of WellKnown API response from WireMock.
     */
    private static void stubWireMockWellKnownResponse() throws JsonProcessingException {
        ProviderMetadata expectedMetadata = ProviderMetadata.builder() //
            .issuer(ISSUER) //
            .authorizationEndpoint(ISSUER + AUTHORIZATION_PATH) //
            .tokenEndpoint(ISSUER + TOKEN_PATH) //
            .jwksUri(ISSUER + JWKS_PATH) //
            .subjectTypesSupported(new String[] { "public" }) //
            .build();

        String expectedMetadataBody = new ObjectMapper().writeValueAsString(expectedMetadata);

        stubFor(get(urlEqualTo(WELLKNOWN_PATH)) //
            .willReturn(aResponse() //
                .withStatus(HttpStatus.OK.value()) //
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) //
                .withBody(expectedMetadataBody)));
    }

    /**
     * Stubbing of Token API response from WireMock.
     */
    private void stubWireMockTokenResponse() throws JsonProcessingException {
        BearerToken bearerToken = BearerToken.builder() //
            .accessToken(TestTokens.Access.valid()) //
            .tokenType("Bearer") //
            .expiresIn(60) //
            .build();

        String expectedTokenBody = objectMapper.writeValueAsString(bearerToken);

        stubFor(post(urlEqualTo(TOKEN_PATH)) //
            .willReturn(aResponse() //
                .withStatus(HttpStatus.OK.value()) //
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) //
                .withBody(expectedTokenBody)));
    }

    /**
     * Test configuration that uses basic RestTemplate/WebClient beans because Discovery-aware beans cannot be used without Eureka.
     */
    @TestConfiguration
    static class TestConfig {

        @Bean
        public RestTemplate discoveryRestTemplate(@Qualifier("basicRestTemplate") RestTemplate restTemplate) {
            return restTemplate;
        }

        @Bean
        public WebClient discoveryWebClient(@Qualifier("basicWebClient") WebClient webClient) {
            return webClient;
        }
    }
}
