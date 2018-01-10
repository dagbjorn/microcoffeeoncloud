package study.microcoffee.location.repository;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * MongoDB implementation of Location repository interface.
 */
@Repository
public class MongoLocationRepository implements LocationRepository {

    private final MongoDbFactory mongo;

    @Autowired
    public MongoLocationRepository(MongoDbFactory mongo) {
        this.mongo = mongo;
    }

    /**
     * Finds the nearest coffee shop within maxdistance meters from the position given by the WGS84 latitude/longitude coordinates.
     * <p>
     * This implementation uses MongoDB's support of GeoJSON. The method creates a geospatial query (shown below) based on the $near
     * operator to find the coffee shop in proximity of the given point.
     *
     * <pre>
     * {location: {$near: {$geometry: {type:        'Point',
     *                                 coordinates: {longitude, latitude}},
     *                     $maxDistance: maxDistance}
     *            }
     * }
     * </pre>
     *
     * @see https://docs.mongodb.com/manual/reference/operator/query/near
     */
    @Override
    public Object findNearestCoffeeShop(double latitude, double longitude, long maxDistance) {
        DBCollection collection = mongo.getDb().getCollection("coffeeshop");

        DBObject coffeeShop = collection.findOne(new BasicDBObject( //
            "location", //
            new BasicDBObject( //
                "$near", //
                new BasicDBObject( //
                    "$geometry", //
                    new BasicDBObject("type", "Point").append("coordinates", Arrays.asList(longitude, latitude))) //
                        .append("$maxDistance", maxDistance))));

        return coffeeShop;
    }
}
