package study.microcoffee.order.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;

/**
 * MongoDB implementation of the Menu repository interface.
 */
@Repository
public class MongoMenuRepository implements MenuRepository {

    private final MongoDatabaseFactory mongo;

    @Autowired
    public MongoMenuRepository(MongoDatabaseFactory mongo) {
        this.mongo = mongo;
    }

    @Override
    public Object getCoffeeMenu() {
        return new BasicDBObject("types", getCollectionAsList("drinktypes")) //
            .append("sizes", getCollectionAsList("drinksizes")) //
            .append("availableOptions", getCollectionAsList("drinkoptions"));
    }

    private List<Object> getCollectionAsList(String collectionName) {
        MongoCollection<Document> collection = mongo.getMongoDatabase().getCollection(collectionName);
        return collection.find().into(new ArrayList<>());
    }
}
