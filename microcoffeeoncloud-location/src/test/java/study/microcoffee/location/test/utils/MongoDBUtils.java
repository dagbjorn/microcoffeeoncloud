package study.microcoffee.location.test.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Utility class for MongoDB operations.
 */
public abstract class MongoDBUtils {

    private static final String COFFEESHOP_COLLECTION_NAME = "coffeeshop";

    public static void loadCoffeeshopCollection(MongoTemplate mongoTemplate, String jsonFileName)
        throws IOException, URISyntaxException {

        URL resource = Thread.currentThread().getContextClassLoader().getResource(jsonFileName);

        try (Stream<String> stream = Files.lines(Paths.get(resource.toURI()), StandardCharsets.UTF_8);) {
            List<String> jsons = new ArrayList<>();

            stream.forEach(jsons::add);
            jsons.stream().map(Document::parse).forEach(doc -> mongoTemplate.insert(doc, COFFEESHOP_COLLECTION_NAME));
        }

        mongoTemplate.getCollection(COFFEESHOP_COLLECTION_NAME).createIndex(new Document("location", "2dsphere"));
    }

    public static void dropCoffeeshopLocation(MongoTemplate mongoTemplate) {
        mongoTemplate.dropCollection(COFFEESHOP_COLLECTION_NAME);
    }
}
