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

import study.microcoffee.order.domain.DrinkType;
import study.microcoffee.order.domain.Order;
import study.microcoffee.order.test.config.MongoTestConfig;
import study.microcoffee.order.test.utils.KeystoreUtils;

/**
 * Integration tests of {@link OrderRepository}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
@Import(MongoTestConfig.class)
public class OrderRepositoryIT {

    @Autowired
    private OrderRepository orderRepository;

    @BeforeClass
    public static void initOnce() throws Exception {
        KeystoreUtils.configureTruststore();
    }

    @AfterClass
    public static void destroyOnce() throws Exception {
        KeystoreUtils.clearTruststore();
    }

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
}
