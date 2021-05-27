package study.microcoffee.location.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import study.microcoffee.location.test.utils.MongoDBUtils;

/**
 * Integration tests of {@link LocationController}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
@ActiveProfiles("itest")
@Profile("itest")
class LocationControllerIT {

    private static final String SERVICE_PATH = "/api/coffeeshop/nearest/{latitude}/{longitude}/{maxdistance}";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void init() throws Exception {
        MongoDBUtils.loadCoffeeshopCollection(mongoTemplate, "testdata/coffeeshop.json");
    }

    @AfterEach
    void destroy() {
        MongoDBUtils.dropCoffeeshopLocation(mongoTemplate);
    }

    @Test
    void getNearestCoffeeShopWhenFoundShouldReturnLocation() {
        ResponseEntity<String> response = restTemplate.getForEntity(SERVICE_PATH, String.class, 59.969048, 10.774445, 2500);

        System.out.println(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("coordinates");
        assertThat(response.getHeaders().getContentType()).asString().startsWith(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void getNearestCoffeeShopWhenNotFoundShouldReturnNoContent() {
        ResponseEntity<String> response = restTemplate.getForEntity(SERVICE_PATH, String.class, 59.969048, 10.774445, 5);

        System.out.println(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
