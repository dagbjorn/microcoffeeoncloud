package study.microcoffee.order.repository;

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

import study.microcoffee.order.test.config.MongoTestConfig;
import study.microcoffee.order.test.utils.KeystoreUtils;

/**
 * Integration tests of {@link MongoMenuRepository}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@Import(MongoTestConfig.class)
public class MongoMenuRepositoryIT {

    @Autowired
    private MenuRepository menuRepository;

    @BeforeClass
    public static void initOnce() throws Exception {
        KeystoreUtils.configureTruststore();
    }

    @AfterClass
    public static void destroyOnce() throws Exception {
        KeystoreUtils.clearTruststore();
    }

    @Test
    public void getCoffeeMenuShouldReturnCoffeeMenu() {
        Object coffeeMenu = menuRepository.getCoffeeMenu();

        System.out.println(coffeeMenu);

        assertThat(coffeeMenu.toString()).contains("Americano");
        assertThat(coffeeMenu.toString()).contains("Small");
        assertThat(coffeeMenu.toString()).contains("soy");
    }
}
