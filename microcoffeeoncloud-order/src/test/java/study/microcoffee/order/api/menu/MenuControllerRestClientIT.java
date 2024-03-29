package study.microcoffee.order.api.menu;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import study.microcoffee.order.SecurityTestConfig;
import study.microcoffee.order.test.DiscoveryTestConfig;

/**
 * Integration tests of {@link MenuController} based on {@link RestClient}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "server.ssl.enabled=false")
@TestPropertySource("/application-test.properties")
@Import({ DiscoveryTestConfig.class, SecurityTestConfig.class })
@ActiveProfiles("itest")
@Profile("itest")
class MenuControllerRestClientIT {

    private static final String SERVICE_PATH = "/api/coffeeshop/menu";

    @LocalServerPort
    private int serverPort;

    @Autowired
    private MongoTemplate mongoTemplate;

    private RestClient restClient;

    @BeforeEach
    void init() {
        MongoDatabase db = mongoTemplate.getDb();

        MongoCollection<Document> collection = db.getCollection("drinktypes");
        collection.insertOne(new Document("name", "Americano").append("family", "Coffee"));
        collection.insertOne(new Document("name", "Latte").append("family", "Coffee"));

        collection = db.getCollection("drinksizes");
        collection.insertOne(new Document("name", "Small"));
        collection.insertOne(new Document("name", "Large"));

        collection = db.getCollection("drinkoptions");
        collection.insertOne(new Document("name", "soy").append("appliesTo", "milk"));
        collection.insertOne(new Document("name", "extra hot").append("appliesTo", "preparation"));

        restClient = RestClient.builder().baseUrl("http://localhost:" + serverPort).build();
    }

    @AfterEach
    void destroy() {
        mongoTemplate.getCollection("drinktypes").drop();
        mongoTemplate.getCollection("drinksizes").drop();
        mongoTemplate.getCollection("drinkoptions").drop();
    }

    @Test
    void getCoffeeMenuShouldReturnCoffeeMenu() {
        ResponseEntity<String> response = restClient.get() //
            .uri(SERVICE_PATH) //
            .retrieve() //
            .toEntity(String.class);

        System.err.println(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Americano");
        assertThat(response.getHeaders().getContentType()).asString().startsWith(MediaType.APPLICATION_JSON_VALUE);
    }
}
