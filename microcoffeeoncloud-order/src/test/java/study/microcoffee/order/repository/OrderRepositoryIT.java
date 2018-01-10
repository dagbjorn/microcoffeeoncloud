package study.microcoffee.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.MongoClient;

import study.microcoffee.order.domain.DrinkType;
import study.microcoffee.order.domain.Order;

/**
 * Integration tests of {@link OrderRepository}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class OrderRepositoryIT {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void saveOrderWhenReadBackShouldReturnSavedOrder() {
        Order order = new Order.Builder() //
            .type(new DrinkType("Latte", "Coffee")) //
            .size("Small") //
            .drinker("Dagbjørn") //
            .selectedOption("skimmed milk") //
            .build();

        Order savedOrder = orderRepository.save(order);

        System.out.println(savedOrder);

        assertThat(savedOrder.getId()).isNotNull();

        Order readbackOrder = orderRepository.findById(savedOrder.getId());

        System.out.println(readbackOrder);

        assertThat(readbackOrder.getId()).isEqualTo(savedOrder.getId());
    }

    @Test
    public void findByIdWhenNoOrderShouldReturnNull() {
        Order order = orderRepository.findById("123");

        assertThat(order).isNull();
    }

    @Configuration
    @EnableMongoRepositories(basePackages = "study.microcoffee.order.repository")
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

        @Bean
        public MongoTemplate mongoTemplate() throws Exception {
            return new MongoTemplate(mongoDbFactory());
        }
    }
}
