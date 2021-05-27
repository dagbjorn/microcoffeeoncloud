package study.microcoffee.order.api.order;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import study.microcoffee.jwttest.TestTokens;
import study.microcoffee.jwttest.oidcprovider.model.BearerToken;
import study.microcoffee.jwttest.oidcprovider.model.ProviderMetadata;
import study.microcoffee.order.api.order.model.OrderModel;
import study.microcoffee.order.consumer.creditrating.CreditRating;
import study.microcoffee.order.domain.DrinkType;

/**
 * Integration tests of {@link OrderController} based on {@link WebTestClient}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMetrics
@TestPropertySource(locations = "/application-test.properties", properties = "server.ssl.enabled=false")
@ActiveProfiles("itest")
@Profile("itest")
class OrderControllerWebClientIT {

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

    private static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(WIREMOCK_PORT));

    // SSL must be disabled to get a correct URL injected. (Otherwise it will be a http URL with SSL port.)
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

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
    void saveOrderAndReadBackShouldReturnSavedOrder() throws Exception {
        // WireMock stubbing of CreditRating API
        final String creditRatingResponse = objectMapper.writeValueAsString(new CreditRating(50));

        stubFor(get(urlPathMatching("/api/coffeeshop/creditrating/(.+)")) //
            .willReturn(aResponse() //
                .withStatus(HttpStatus.OK.value()) //
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE) //
                .withBody(creditRatingResponse)));

        OrderModel newOrder = createOrder();

        EntityExchangeResult<OrderModel> response = webTestClient.post() //
            .uri(POST_SERVICE_PATH, COFFEE_SHOP_ID) //
            .header("X-Forwarded-Host", "forwardedhost.no") //
            .contentType(MediaType.APPLICATION_JSON) //
            .bodyValue(newOrder) //
            .exchange() //
            .expectStatus().isCreated() //
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE) //
            .expectHeader().value(HttpHeaders.LOCATION, containsString("forwardedhost.no")) //
            .expectBody(OrderModel.class) //
            .consumeWith(result -> {
                assertThat(result.getResponseHeaders().getLocation().toString()).endsWith(result.getResponseBody().getId());
                assertThat(result.getResponseBody().getType()).isEqualTo(newOrder.getType());
                assertThat(result.getResponseBody().getDrinker()).isEqualTo(newOrder.getDrinker());
            }) //
            .returnResult();

        OrderModel savedOrder = response.getResponseBody();

        webTestClient.get() //
            .uri(GET_SERVICE_PATH, COFFEE_SHOP_ID, savedOrder.getId()) //
            .accept(MediaType.APPLICATION_JSON) //
            .exchange() //
            .expectStatus().isOk() //
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE) //
            .expectBody(OrderModel.class) //
            .consumeWith(result -> {
                assertThat(result.getResponseBody()).hasToString(savedOrder.toString());
            });
    }

    @Test
    @EnabledIf("isBasicConsumer")
    void saveOrderWhenCreditRatingNotAvailableShouldFail() throws Exception {
        stubFor(get(urlPathMatching("/api/coffeeshop/creditrating/(.+)")) //
            .willReturn(status(HttpStatus.SERVICE_UNAVAILABLE.value())));

        webTestClient.post() //
            .uri(POST_SERVICE_PATH, COFFEE_SHOP_ID) //
            .contentType(MediaType.APPLICATION_JSON) //
            .bodyValue(createOrder()) //
            .exchange() //
            .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @EnabledIf("isResilience4jConsumer")
    void saveOrderWhenCreditRatingNotAvailableShouldFailAfterRetry() throws Exception {
        float currentFailedWithRetryCount = getMetricValue(PROMETHEUS_METRIC_FAILED_WITH_RETRY);

        stubWireMockTokenResponse();

        stubFor(get(urlPathMatching("/api/coffeeshop/creditrating/(.+)")) //
            .willReturn(status(HttpStatus.SERVICE_UNAVAILABLE.value())));

        webTestClient.post() //
            .uri(POST_SERVICE_PATH, COFFEE_SHOP_ID) //
            .contentType(MediaType.APPLICATION_JSON) //
            .bodyValue(createOrder()) //
            .exchange() //
            .expectStatus().isEqualTo(HttpStatus.PAYMENT_REQUIRED);

        assertThat(getMetricValue(PROMETHEUS_METRIC_FAILED_WITH_RETRY)).isEqualTo(currentFailedWithRetryCount + 1);
    }

    @Test
    void getOrderWhenNoOrderShouldReturnNoContent() throws Exception {
        String orderId = "1111111111111111";

        webTestClient.get() //
            .uri(GET_SERVICE_PATH, COFFEE_SHOP_ID, orderId) //
            .accept(MediaType.APPLICATION_JSON) //
            .exchange() //
            .expectStatus().isNoContent();
    }

    private OrderModel createOrder() {
        return OrderModel.builder() //
            .type(new DrinkType("Latte", "Coffee")) //
            .size("Small") //
            .drinker("Dagbjørn") //
            .selectedOptions(new String[] { "skimmed milk" }) //
            .build();
    }

    /**
     * Returns a properly formatted Prometheus metric.
     */
    private static String getMetricName(String metric, String application, String kind, String backend) {
        return metric + "{application=\"" + application + "\",kind=\"" + kind + "\",name=\"" + backend + "\",} ";
    }

    /**
     * Reads the current metric value from the Actuator Prometheus endpoint.
     */
    private float getMetricValue(String key) {
        EntityExchangeResult<String> response = webTestClient.get() //
            .uri("/actuator/prometheus") //
            .exchange() //
            .expectStatus().isOk() //
            .expectBody(String.class) //
            .returnResult();

        String value = response.getResponseBody().lines().filter(line -> line.startsWith(key)).findFirst().get().split("\\s")[1];
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
