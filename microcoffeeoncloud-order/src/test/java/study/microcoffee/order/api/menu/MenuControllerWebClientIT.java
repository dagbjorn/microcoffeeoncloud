package study.microcoffee.order.api.menu;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import study.microcoffee.order.SecurityTestConfig;
import study.microcoffee.order.test.DiscoveryTestConfig;

/**
 * Integration tests of {@link MenuController} based on {@link WebTestClient}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestPropertySource(locations = "/application-test.properties")
@Import({ DiscoveryTestConfig.class, SecurityTestConfig.class })
@ActiveProfiles("itest")
@Profile("itest")
class MenuControllerWebClientIT {

    private static final String SERVICE_PATH = "/api/coffeeshop/menu";

    @Autowired
    private MongoTemplate mongoTemplate;

    // SSL must be disabled to get a correct URL injected. (Otherwise it will be a http URL with SSL port.)
    @Autowired
    private WebTestClient webTestClient;

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
    }

    @AfterEach
    void destroy() {
        mongoTemplate.getCollection("drinktypes").drop();
        mongoTemplate.getCollection("drinksizes").drop();
        mongoTemplate.getCollection("drinkoptions").drop();
    }

    @Test
    void getCoffeeMenuShouldReturnCoffeeMenu() {
        EntityExchangeResult<String> response = webTestClient.get() //
            .uri(SERVICE_PATH) //
            .accept(MediaType.APPLICATION_JSON) //
            .exchange() //
            .expectStatus().isOk() //
            .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE) //
            .expectBody(String.class) //
            .consumeWith(result -> {
                assertThat(result.getResponseBody()).contains("Americano");
            }) //
            .returnResult();

        System.err.println(response.getResponseBody());
    }
}
