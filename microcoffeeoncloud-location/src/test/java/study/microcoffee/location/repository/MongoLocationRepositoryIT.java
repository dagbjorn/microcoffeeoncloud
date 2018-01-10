package study.microcoffee.location.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.MongoClient;

/**
 * Integration tests of {@link MongoLocationRepository}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class MongoLocationRepositoryIT {

    @Autowired
    private LocationRepository locationRepository;

    @Test
    public void findNearestCoffeeShopShouldReturnLocation() {
        Object coffeeShop = locationRepository.findNearestCoffeeShop(59.969048, 10.774445, 2500);

        System.out.println(coffeeShop);

        assertThat(coffeeShop.toString()).contains("coordinates");
    }

    @Test
    public void findNearestCoffeeShopWhenNotFoundShouldReturnNull() {
        Object coffeeShop = locationRepository.findNearestCoffeeShop(59.969048, 10.774445, 10); // within 10 meters

        System.out.println(coffeeShop);

        assertThat(coffeeShop).isNull();
    }

    @Configuration
    @Import({ MongoLocationRepository.class })
    static class Config {

        @Value("${mongo.database.host}")
        private String mongoDatabaseHost;

        @Value("${mongo.database.port}")
        private int mongoDatabasePort;

        @Value("${mongo.database.name}")
        private String mongoDatabaseName;

        @Bean
        public MongoDbFactory mongoDbFactory() {
            return new SimpleMongoDbFactory(new MongoClient(mongoDatabaseHost, mongoDatabasePort), mongoDatabaseName);
        }
    }
}
