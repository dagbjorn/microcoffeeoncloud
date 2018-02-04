package study.microcoffee.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.MongoClient;

/**
 * Integration tests of {@link MongoMenuRepository}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class MongoMenuRepositoryIT {

    @Autowired
    private MenuRepository menuRepository;

    @Test
    public void getCoffeeMenuShouldReturnCoffeeMenu() {
        Object coffeeMenu = menuRepository.getCoffeeMenu();

        System.out.println(coffeeMenu);

        assertThat(coffeeMenu.toString()).contains("Americano");
        assertThat(coffeeMenu.toString()).contains("Small");
        assertThat(coffeeMenu.toString()).contains("soy");
    }

    @TestConfiguration
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
