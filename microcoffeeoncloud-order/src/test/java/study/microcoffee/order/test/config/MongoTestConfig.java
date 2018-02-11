package study.microcoffee.order.test.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

/**
 * Test configuration class for Mongo DB.
 */
@TestConfiguration
public class MongoTestConfig {

    @Value("${mongo.database.host}")
    private String mongoDatabaseHost;

    @Value("${mongo.database.port}")
    private int mongoDatabasePort;

    @Value("${mongo.database.name}")
    private String mongoDatabaseName;

    @Value("${mongo.database.ssl}")
    private boolean mongoDatabaseSsl;

    @Bean
    public MongoClientOptions mongoClientOptions() {
        return MongoClientOptions.builder() //
            .sslEnabled(mongoDatabaseSsl) //
            .build();
    }

    @Bean
    public MongoDbFactory mongoDbFactory() {
        ServerAddress serverAddress = new ServerAddress(mongoDatabaseHost, mongoDatabasePort);

        return new SimpleMongoDbFactory(new MongoClient(serverAddress, mongoClientOptions()), mongoDatabaseName);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoDbFactory());
    }
}
