package study.microcoffee.creditrating.api;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import study.microcoffee.creditrating.domain.CreditRating;
import study.microcoffee.jwttest.TestTokens;
import study.microcoffee.jwttest.oidcprovider.model.Jwk;
import study.microcoffee.jwttest.oidcprovider.model.JwkSet;
import study.microcoffee.jwttest.oidcprovider.model.ProviderMetadata;

/**
 * Integration tests of {@link CreditRatingController}.
 * <p>
 * The tests are using WireMock to mock responses from the WellKnown and JKWS endpoints. The RSA public key returned is the key of
 * the localhost certificate in the test keystore. The access tokens are signed with the corresponding private key.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class CreditRatingControllerIT {

    private static final int WIREMOCK_PORT = 9999;

    // Issuer must match value in application-test.properties.
    private static final String ISSUER = "http://localhost:" + WIREMOCK_PORT;
    private static final String WELLKNOWN_PATH = "/.well-known/openid-configuration";
    private static final String JWKS_PATH = "/protocol/openid-connect/certs";

    private static final String SERVICE_PATH = "/api/coffeeshop/creditrating/{customerId}";

    private static final String CUSTOMER_ID = "Dagbjørn";

    private static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().port(WIREMOCK_PORT));

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public static void beforeAll() throws Exception {
        wireMockServer.start();

        // Client-side configuration (default port is 8080)
        WireMock.configureFor(WIREMOCK_PORT);

        // Stub response of OIDC Discovery using WellKnown URL. Must be done before Spring context is created.
        stubWireMockWellKnownResponse();
    }

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        stubWireMockJwksResponse();
    }

    @AfterEach
    public void afterEach() {
        wireMockServer.resetAll();
    }

    @Test
    public void getCreditRatingShouldReturnRating() throws Exception {
        String accessToken = TestTokens.Access.custom() //
            .withAudience("creditrating") //
            .withClaim("scope", "creditrating") //
            .sign(TestTokens.Access.algorithm());

        HttpEntity<Void> entity = createHttpEntity(accessToken);

        ResponseEntity<CreditRating> response = restTemplate.exchange(SERVICE_PATH, HttpMethod.GET, entity, CreditRating.class,
            CUSTOMER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getRating()).isEqualTo(70);
    }

    @Test
    public void getCreditRatingWhenMissingAudienceShouldReturn401() throws Exception {
        String accessToken = TestTokens.Access.custom() //
            .withClaim("scope", "creditrating") //
            .sign(TestTokens.Access.algorithm());

        HttpEntity<Void> entity = createHttpEntity(accessToken);

        ResponseEntity<CreditRating> response = restTemplate.exchange(SERVICE_PATH, HttpMethod.GET, entity, CreditRating.class,
            CUSTOMER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void getCreditRatingWhenMissingScopeShouldReturn403() throws Exception {
        String accessToken = TestTokens.Access.custom() //
            .withAudience("creditrating") //
            .sign(TestTokens.Access.algorithm());

        HttpEntity<Void> entity = createHttpEntity(accessToken);

        ResponseEntity<CreditRating> response = restTemplate.exchange(SERVICE_PATH, HttpMethod.GET, entity, CreditRating.class,
            CUSTOMER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    public void getCreditRatingWhenExpiredTokenShouldReturn401() throws Exception {
        HttpEntity<Void> entity = createHttpEntity(TestTokens.Access.expired());

        ResponseEntity<CreditRating> response = restTemplate.exchange(SERVICE_PATH, HttpMethod.GET, entity, CreditRating.class,
            CUSTOMER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Creates an HttpEntity object with required headers.
     */
    private HttpEntity<Void> createHttpEntity(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        return new HttpEntity<>(headers);
    }

    /**
     * Static stubbing of WellKnown API response from WireMock.
     */
    private static void stubWireMockWellKnownResponse() throws JsonProcessingException {
        ProviderMetadata expectedMetadata = ProviderMetadata.builder() //
            .issuer(ISSUER) //
            .jwksUri(ISSUER + JWKS_PATH) //
            .build();

        String expectedMetadataBody = new ObjectMapper().writeValueAsString(expectedMetadata);

        stubFor(get(urlEqualTo(WELLKNOWN_PATH)) //
            .willReturn(aResponse() //
                .withStatus(HttpStatus.OK.value()) //
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) //
                .withBody(expectedMetadataBody)));
    }

    /**
     * Stubbing of JWKS API response from WireMock.
     */
    private void stubWireMockJwksResponse() throws JsonProcessingException {
        JwkSet expectedJwkSet = JwkSet.builder() //
            .keys(Arrays.asList(Jwk.builder() //
                .kty(TestTokens.KEY_TYPE) //
                .kid(TestTokens.KEY_ALIAS) //
                .n(TestTokens.PublicKey.getModulusAsBase64()) //
                .e(TestTokens.PublicKey.getExponentAsBase64()) //
                .build()))
            .build();

        String expectedJwkSetBody = objectMapper.writeValueAsString(expectedJwkSet);

        stubFor(get(urlEqualTo(JWKS_PATH)) //
            .willReturn(aResponse() //
                .withStatus(HttpStatus.OK.value()) //
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) //
                .withBody(expectedJwkSetBody)));
    }
}
