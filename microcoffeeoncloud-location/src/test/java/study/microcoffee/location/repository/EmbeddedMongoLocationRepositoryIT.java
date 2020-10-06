package study.microcoffee.location.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import study.microcoffee.location.test.utils.MongoDBUtils;

/**
 * Integration tests of {@link MongoLocationRepository}.
 * <p>
 * Spring Boot autoconfigures a MongoTemplate instance when de.flapdoodle.embed.mongo is found on the classpath.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class EmbeddedMongoLocationRepositoryIT {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private LocationRepository locationRepository;

    @Before
    public void init() throws Exception {
        MongoDBUtils.loadCoffeeshopCollection(mongoTemplate, "testdata/coffeeshop.json");
    }

    @After
    public void destroy() {
        MongoDBUtils.dropCoffeeshopLocation(mongoTemplate);
    }

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
}
