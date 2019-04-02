package study.microcoffee.order.api.order;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import study.microcoffee.order.consumer.creditrating.CreditRating;
import study.microcoffee.order.domain.DrinkType;
import study.microcoffee.order.domain.Order;

/**
 * Integration tests of {@link OrderController}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@TestPropertySource("/application-test.properties")
@AutoConfigureWireMock(port = 8083) // HTTP port of CreditRating API
@ActiveProfiles("itest")
@Profile("itest")
public class OrderControllerIT {

    private static final String POST_SERVICE_PATH = "/api/coffeeshop/{coffeeShopId}/order";
    private static final String GET_SERVICE_PATH = "/api/coffeeshop/{coffeeShopId}/order/{orderId}";

    private static final int COFFEE_SHOP_ID = 10;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void saveOrderAndReadBackShouldReturnSavedOrder() throws Exception {
        // WireMock stubbing of CreditRating API
        final String creditRatingResponse = objectMapper.writeValueAsString(new CreditRating(50));

        stubFor(get(urlPathMatching("/api/coffeeshop/creditrating/(.+)")) //
            .willReturn(aResponse() //
                .withStatus(HttpStatus.OK.value()) //
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE) //
                .withBody(creditRatingResponse)));

        Order newOrder = Order.builder() //
            .type(new DrinkType("Latte", "Coffee")) //
            .size("Small") //
            .drinker("Dagbjørn") //
            .selectedOptions(new String[] { "skimmed milk" }) //
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Forwarded-Host", "forwardedhost.no");
        HttpEntity<Order> requestEntity = new HttpEntity<Order>(newOrder, headers);

        ResponseEntity<Order> response = restTemplate.exchange(POST_SERVICE_PATH, HttpMethod.POST, requestEntity, Order.class,
            COFFEE_SHOP_ID);

        Order savedOrder = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat(response.getHeaders().getLocation().toString()).contains("forwardedhost.no");
        assertThat(response.getHeaders().getLocation().toString()).endsWith(savedOrder.getId());
        assertThat(savedOrder.getType().getName()).isEqualTo("Latte");
        assertThat(savedOrder.getDrinker()).isEqualTo("Dagbjørn");

        response = restTemplate.getForEntity(GET_SERVICE_PATH, Order.class, COFFEE_SHOP_ID, savedOrder.getId());

        Order readBackOrder = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        assertThat(readBackOrder.toString()).isEqualTo(savedOrder.toString());
    }

    @Test
    public void getOrderWhenNoOrderShouldReturnNoContent() throws Exception {
        String orderId = "1111111111111111";

        ResponseEntity<Order> response = restTemplate.getForEntity(GET_SERVICE_PATH, Order.class, COFFEE_SHOP_ID, orderId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
