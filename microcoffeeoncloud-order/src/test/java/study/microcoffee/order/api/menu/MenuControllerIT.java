package study.microcoffee.order.api.menu;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Integration tests of {@link MenuController}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application-test.properties")
@ActiveProfiles("itest")
@Profile("itest")
public class MenuControllerIT {

    private static final String SERVICE_PATH = "/api/coffeeshop/menu";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void init() {
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

    @After
    public void destroy() {
        mongoTemplate.getCollection("drinktypes").drop();
        mongoTemplate.getCollection("drinksizes").drop();
        mongoTemplate.getCollection("drinkoptions").drop();
    }

    @Test
    public void getCoffeeMenuShouldReturnCoffeeMenu() {
        ResponseEntity<String> response = restTemplate.getForEntity(SERVICE_PATH, String.class);

        System.out.println(response);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Americano");
        assertThat(response.getHeaders().getContentType()).asString().startsWith(MediaType.APPLICATION_JSON_VALUE);
    }
}
