package study.microcoffee.location.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import study.microcoffee.location.test.config.MongoTestConfig;
import study.microcoffee.location.test.utils.KeystoreUtils;

/**
 * Integration tests of {@link MongoLocationRepository}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@Import(MongoTestConfig.class)
public class MongoLocationRepositoryIT {

    @Autowired
    private LocationRepository locationRepository;

    @BeforeClass
    public static void initOnce() throws Exception {
        KeystoreUtils.configureTruststore();
    }

    @AfterClass
    public static void destroyOnce() throws Exception {
        KeystoreUtils.clearTruststore();
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
