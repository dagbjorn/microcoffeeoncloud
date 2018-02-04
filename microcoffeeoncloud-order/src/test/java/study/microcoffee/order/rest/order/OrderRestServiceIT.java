package study.microcoffee.order.rest.order;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.MongoClient;

import study.microcoffee.order.domain.DrinkType;
import study.microcoffee.order.domain.Order;

/**
 * Integration tests of {@link OrderRestService}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class OrderRestServiceIT {

    private static final String POST_SERVICE_PATH = "/coffeeshop/{coffeeShopId}/order";
    private static final String GET_SERVICE_PATH = "/coffeeshop/{coffeeShopId}/order/{orderId}";

    private static final int COFFEE_SHOP_ID = 10;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void saveOrderAndReadBackShouldReturnSavedOrder() throws Exception {
        Order newOrder = new Order.Builder() //
            .type(new DrinkType("Latte", "Coffee")) //
            .size("Small") //
            .drinker("Dagbjørn") //
            .selectedOption("skimmed milk") //
            .build();

        ResponseEntity<Order> response = restTemplate.postForEntity(POST_SERVICE_PATH, newOrder, Order.class, COFFEE_SHOP_ID);

        Order savedOrder = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
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

    @TestConfiguration
    static class Config {

        @Value("${mongo.database.host}")
        private String mongoDatabaseHost;

        @Value("${mongo.database.port}")
        private int mongoDatabasePort;

        @Value("${mongo.database.name}")
        private String mongoDatabaseName;

        @Bean
        public MongoDbFactory mongoDbFactory() {
            return new SimpleMongoDbFactory(new MongoClient(mongoDatabaseHost, mongoDatabasePort), mongoDatabaseName);
        }

        @Bean
        public MongoTemplate mongoTemplate() throws Exception {
            return new MongoTemplate(mongoDbFactory());
        }
    }
}
