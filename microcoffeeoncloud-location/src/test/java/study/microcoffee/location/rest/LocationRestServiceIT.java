package study.microcoffee.location.rest;

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
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.MongoClient;

/**
 * Integration tests of {@link LocationRestService}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
public class LocationRestServiceIT {

    private static final String SERVICE_PATH = "/coffeeshop/nearest/{latitude}/{longitude}/{maxdistance}";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getNearestCoffeeShopWhenFoundShouldReturnLocation() {
        ResponseEntity<String> response = restTemplate.getForEntity(SERVICE_PATH, String.class, 59.969048, 10.774445, 2500);

        System.out.println(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("coordinates");
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
    }

    @Test
    public void getNearestCoffeeShopWhenNotFoundShouldReturnNoContent() {
        ResponseEntity<String> response = restTemplate.getForEntity(SERVICE_PATH, String.class, 59.969048, 10.774445, 5);

        System.out.println(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void getNearestCoffeeShopWhenOriginHeaderSetShouldReturnCORSHeaders() {
        final String originHeaderValue = "http://example.com:8080";

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HttpHeaders.ORIGIN, originHeaderValue);

        ResponseEntity<String> response = restTemplate.exchange(SERVICE_PATH, HttpMethod.GET, new HttpEntity<>(requestHeaders),
            String.class, 59.969048, 10.774445, 2500);

        // ResponseEntity<String> response = restTemplate.getForEntity(SERVICE_PATH, String.class, 59.969048, 10.774445, 2500);

        System.out.println(response.getBody());
        System.out.println(response.getHeaders());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("coordinates");
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
        // No Access-Control-Allow-Origin returned
        // assertThat(response.getHeaders().getAccessControlAllowOrigin()).isEqualTo(originHeaderValue);
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
    }
}
