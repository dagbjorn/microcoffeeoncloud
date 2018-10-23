package study.microcoffee.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Integration tests of {@link MongoMenuRepository}.
 * <p>
 * Spring Boot autoconfigures a MongoTemplate instance when de.flapdoodle.embed.mongo is found on the classpath.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class EmbeddedMongoMenuRepositoryIT {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MenuRepository menuRepository;

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
        Object coffeeMenu = menuRepository.getCoffeeMenu();

        System.out.println(coffeeMenu);

        assertThat(coffeeMenu.toString()).contains("Americano");
        assertThat(coffeeMenu.toString()).contains("Large");
        assertThat(coffeeMenu.toString()).contains("extra hot");
    }
}
