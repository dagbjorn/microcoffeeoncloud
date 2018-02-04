package study.microcoffee.order;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Configuration class of Mongo DB.
 */
@Configuration
@EnableMongoRepositories(basePackages = "study.microcoffee.order.repository")
public class MongoConfig {

}
