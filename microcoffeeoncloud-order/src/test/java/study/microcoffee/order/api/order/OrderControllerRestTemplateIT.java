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

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.micrometer.metrics.test.autoconfigure.AutoConfigureMetrics;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
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
import org.springframework.web.client.RestClient;
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
import study.microcoffee.order.consumer.creditrating.BasicRestClientCreditRatingConsumer;
import study.microcoffee.order.consumer.creditrating.BasicRestTemplateCreditRatingConsumer;
import study.microcoffee.order.consumer.creditrating.BasicWebClientCreditRatingConsumer;
import study.microcoffee.order.consumer.creditrating.CreditRating;
import study.microcoffee.order.consumer.creditrating.Resilience4JRestClientCreditRatingConsumer;
import study.microcoffee.order.consumer.creditrating.Resilience4JRestTemplateCreditRatingConsumer;
import study.microcoffee.order.consumer.creditrating.Resilience4JWebClientCreditRatingConsumer;
import study.microcoffee.order.domain.DrinkType;

/**
 * Integration tests of {@link OrderController}.
 * <p>
 * The integration tests mimics the behaviour of CSRF protection with Spring CookieCsrfTokenRepository which works as follows:
 * <ol>
 * <li>Client makes a GET request to server (Spring backend), e.g. request for the main page.</li>
 * <li>Spring sends the response for GET request along with Set-cookie header which contains securely generated XSRF Token.</li>
 * <li>Browser sets the cookie with XSRF Token.</li>
 * <li>While sending state changing request (e.g. POST), the client copies the cookie value to the HTTP request header.</li>
 * <li>The request is sent with both header and cookie (browser attaches the cookie automatically).</li>
 * <li>Spring compares the header and the cookie values, if they are the same the request is accepted, otherwise 403 is returned to
 * the client.</li>
 * </ol>
 * Source: <a href=
 * "https://stackoverflow.com/questions/40034430/will-spring-security-csrf-token-repository-cookies-work-for-all-ajax-requests-au">Answer
 * by user frenchu to this post on stackoverflow</a>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@AutoConfigureMetrics
@TestPropertySource("/application-test.properties")
@ActiveProfiles("itest")
@Profile("itest")
class OrderControllerRestTemplateIT {

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

    private static final String PROMETHEUS_METRIC_FAILED_WITH_RETRY = getMetricName("resilience4j_retry_calls_total",
        "failed_with_retry", "creditRating");

    private static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(WIREMOCK_PORT));

    @Autowired
    private TestRestTemplate restTemplate;

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

        ResponseEntity<OrderModel> response = restTemplate.exchange(POST_SERVICE_PATH, HttpMethod.POST,
            createRequestEntity(newOrder), OrderModel.class, COFFEE_SHOP_ID);

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
    void createOrderWhenCreditRatingNotAvailableShouldFail() {
        stubFor(get(urlPathMatching("/api/coffeeshop/creditrating/(.+)")) //
            .willReturn(status(HttpStatus.SERVICE_UNAVAILABLE.value())));

        ResponseEntity<OrderModel> response = restTemplate.exchange(POST_SERVICE_PATH, HttpMethod.POST,
            createRequestEntity(createOrder()), OrderModel.class, COFFEE_SHOP_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Disabled("TODO Fix Prometheus No value present")
    @Test
    @EnabledIf("isResilience4jConsumer")
    void createOrderWhenCreditRatingNotAvailableShouldFailAfterRetry() {
        float currentFailedWithRetryCount = getMetricValueFromPrometheus(PROMETHEUS_METRIC_FAILED_WITH_RETRY);

        stubFor(get(urlPathMatching("/api/coffeeshop/creditrating/(.+)")) //
            .willReturn(status(HttpStatus.SERVICE_UNAVAILABLE.value())));

        ResponseEntity<OrderModel> response = restTemplate.exchange(POST_SERVICE_PATH, HttpMethod.POST,
            createRequestEntity(createOrder()), OrderModel.class, COFFEE_SHOP_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PAYMENT_REQUIRED);
        assertThat(getMetricValueFromPrometheus(PROMETHEUS_METRIC_FAILED_WITH_RETRY)).isEqualTo(currentFailedWithRetryCount + 1);
    }

    @Test
    void getOrderWhenNoOrderShouldReturnNoContent() {
        String orderId = "1111111111111111";

        ResponseEntity<OrderModel> response = restTemplate.getForEntity(GET_SERVICE_PATH, OrderModel.class, COFFEE_SHOP_ID,
            orderId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    //
    // Helper methods
    //

    private HttpEntity<OrderModel> createRequestEntity(OrderModel order) {
        String csrfToken = getCurrentCsrfTokenFromApi();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Forwarded-Host", "forwardedhost.no");
        headers.add("X-XSRF-TOKEN", csrfToken);
        headers.add(HttpHeaders.COOKIE, "XSRF-TOKEN" + "=" + csrfToken);
        return new HttpEntity<>(order, headers);
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
     * Gets the current CSRF token by doing a GET operation to the API. The CSRF token is returned in a X-XSRF-TOKEN header.
     */
    private String getCurrentCsrfTokenFromApi() {
        ResponseEntity<OrderModel> response = restTemplate.getForEntity(GET_SERVICE_PATH, OrderModel.class, COFFEE_SHOP_ID, "123");

        List<String> csrfTokenList = response.getHeaders().getValuesAsList("X-XSRF-TOKEN");
        assertThat(csrfTokenList).isNotEmpty();

        return csrfTokenList.get(0);
    }

    private static String getMetricName(String metric, String kind, String backend) {
        return metric + "{kind=\"" + kind + "\",name=\"" + backend + "\"} ";
    }

    private float getMetricValueFromPrometheus(String key) {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/prometheus", String.class);

        String value = response.getBody().lines().filter(line -> line.startsWith(key)).findFirst().get().split("\\s")[1];
        return Float.valueOf(value);
    }

    //
    // Used by @EnableIf annotations
    //

    @SuppressWarnings("unused")
    private boolean isBasicConsumer() {
        return OrderController.CREDIT_RATING_CONSUMER.equals(BasicRestTemplateCreditRatingConsumer.CONSUMER_TYPE)
            || OrderController.CREDIT_RATING_CONSUMER.equals(BasicRestClientCreditRatingConsumer.CONSUMER_TYPE)
            || OrderController.CREDIT_RATING_CONSUMER.equals(BasicWebClientCreditRatingConsumer.CONSUMER_TYPE);
    }

    @SuppressWarnings("unused")
    private boolean isResilience4jConsumer() {
        return OrderController.CREDIT_RATING_CONSUMER.equals(Resilience4JRestTemplateCreditRatingConsumer.CONSUMER_TYPE)
            || OrderController.CREDIT_RATING_CONSUMER.equals(Resilience4JRestClientCreditRatingConsumer.CONSUMER_TYPE)
            || OrderController.CREDIT_RATING_CONSUMER.equals(Resilience4JWebClientCreditRatingConsumer.CONSUMER_TYPE);
    }

    //
    // WireMock stubbing
    //

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
     * Test configuration that uses basic RestTemplate/RestClient/WebClient beans because Discovery-aware beans cannot be used
     * without Eureka.
     */
    @TestConfiguration
    static class TestConfig {

        @Bean
        public RestTemplate discoveryRestTemplate(@Qualifier("basicRestTemplate") RestTemplate restTemplate) {
            return restTemplate;
        }

        @Bean
        public RestClient.Builder discoveryRestClientBuilder(
            @Qualifier("basicRestClientBuilder") RestClient.Builder restclientBuilder) {
            return restclientBuilder;
        }

        @Bean
        public WebClient discoveryWebClient(@Qualifier("basicWebClient") WebClient webClient) {
            return webClient;
        }
    }
}
