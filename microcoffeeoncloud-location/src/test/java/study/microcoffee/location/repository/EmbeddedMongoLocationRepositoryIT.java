package study.microcoffee.location.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;

import study.microcoffee.location.test.utils.MongoDBUtils;

/**
 * Integration tests of {@link MongoLocationRepository}.
 * <p>
 * Spring Boot autoconfigures a MongoTemplate instance when de.flapdoodle.embed.mongo is found on the classpath.
 */
@Disabled("Flapdoodle Embedded MongoDB still doesn't support Spring Boot 4")
@SpringBootTest
@TestPropertySource("/application-test.properties")
class EmbeddedMongoLocationRepositoryIT {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private LocationRepository locationRepository;

    @BeforeEach
    void init() throws Exception {
        MongoDBUtils.loadCoffeeshopCollection(mongoTemplate, "testdata/coffeeshop.json");
    }

    @AfterEach
    void destroy() {
        MongoDBUtils.dropCoffeeshopLocation(mongoTemplate);
    }

    @Test
    void findNearestCoffeeShopShouldReturnLocation() {
        Object coffeeShop = locationRepository.findNearestCoffeeShop(59.969048, 10.774445, 2500);

        System.out.println(coffeeShop);

        assertThat(coffeeShop.toString()).contains("coordinates");
    }

    @Test
    void findNearestCoffeeShopWhenNotFoundShouldReturnNull() {
        Object coffeeShop = locationRepository.findNearestCoffeeShop(59.969048, 10.774445, 10); // within 10 meters

        System.out.println(coffeeShop);

        assertThat(coffeeShop).isNull();
    }
}
