package study.microcoffee.order.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

/**
 * MongoDB implementation of the Menu repository interface.
 */
@Repository
public class MongoMenuRepository implements MenuRepository {

    private final MongoDbFactory mongo;

    @Autowired
    public MongoMenuRepository(MongoDbFactory mongo) {
        this.mongo = mongo;
    }

    @Override
    public Object getCoffeeMenu() {
        // String menu = "{" //
        // + "\"types\": " //
        // + "[{\"name\": \"Americano\", \"family\": \"Coffee\"}," //
        // + "{\"name\": \"Latte\", \"family\": \"Coffee\"}," //
        // + "{\"name\": \"Tea\", \"family\": \"That other drink\"}," //
        // + "{\"name\": \"Cappuccino\", \"family\": \"Coffee\"}]," //
        //
        // + "\"sizes\": " //
        // + "[{\"name\": \"Small\"}," //
        // + "{\"name\": \"Medium\"}," //
        // + "{\"name\": \"Large\"}," //
        // + "{\"name\": \"X-Large\"}," //
        // + "{\"name\": \"Supersized\"}]" //
        //
        // + "\"availableOptions\": " //
        // + "[{\"name\": \"soy\", \"appliesTo\": \"milk\"}," //
        // + "{\"name\": \"skimmed\", \"appliesTo\": \"milk\"}," //
        // + "{\"name\": \"caramel\", \"appliesTo\": \"syrup\"}," //
        // + "{\"name\": \"decaf\", \"appliesTo\": \"caffeine\"}," //
        // + "{\"name\": \"whipped cream\", \"appliesTo\": \"extras\"}," //
        // + "{\"name\": \"vanilla\", \"appliesTo\": \"syrup\"}," //
        // + "{\"name\": \"hazelnut\", \"appliesTo\": \"syrup\"}," //
        // + "{\"name\": \"sugar free\", \"appliesTo\": \"syrup\"}," //
        // + "{\"name\": \"non fat\", \"appliesTo\": \"milk\"}," //
        // + "{\"name\": \"half fat\", \"appliesTo\": \"milk\"}," //
        // + "{\"name\": \"half and half\", \"appliesTo\": \"milk\"}," //
        // + "{\"name\": \"half caf\", \"appliesTo\": \"caffeine\"}," //
        // + "{\"name\": \"chocolate powder\", \"appliesTo\": \"extras\"}," //
        // + "{\"name\": \"double shot\", \"appliesTo\": \"preparation\"}," //
        // + "{\"name\": \"wet\", \"appliesTo\": \"preparation\"}," //
        // + "{\"name\": \"dry\", \"appliesTo\": \"preparation\"}," //
        // + "{\"name\": \"organic\", \"appliesTo\": \"milk\"}," //
        // + "{\"name\": \"extra hot\", \"appliesTo\": \"preparation\"}]" //
        // + "}";

        Object menu = new BasicDBObject("types", getCollectionAsList("drinktypes")) //
            .append("sizes", getCollectionAsList("drinksizes")) //
            .append("availableOptions", getCollectionAsList("drinkoptions"));

        return menu;
    }

    private List<Object> getCollectionAsList(String collectionName) {
        MongoCollection<Document> collection = mongo.getDb().getCollection(collectionName);
        return collection.find().into(new ArrayList<>());
    }
}
