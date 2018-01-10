package study.microcoffee.order.rest.menu;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.MongoClient;

import study.microcoffee.order.repository.MongoMenuRepository;

/**
 * Integration tests of {@link MenuRestService}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = { "/application-test.properties" }, properties = { "server.ssl.enabled=false" })
public class MenuRestServiceIT {

    private static final String SERVICE_PATH = "/coffeeshop/menu";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getCoffeeMenuShouldReturnCoffeeMenu() {
        ResponseEntity<String> response = restTemplate.getForEntity(SERVICE_PATH, String.class);

        System.out.println(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Americano");
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON_UTF8);
    }

    @SpringBootApplication
    @Import({ MenuRestService.class, MongoMenuRepository.class })
    @EnableMongoRepositories(basePackages = "study.microcoffee.order.repository")
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
