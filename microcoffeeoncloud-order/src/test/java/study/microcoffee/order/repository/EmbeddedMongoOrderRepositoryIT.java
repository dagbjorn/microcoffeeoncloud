package study.microcoffee.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.Repository;
import org.springframework.test.context.TestPropertySource;

import study.microcoffee.order.domain.DrinkType;
import study.microcoffee.order.domain.Order;
import study.microcoffee.order.test.DiscoveryRestTemplateTestConfig;

/**
 * Integration tests of {@link OrderRepository} that uses an auto-configured Embedded MongoDB database.
 * <p>
 * The @DataMongoTest annotation will scan for @Document classes and Spring {@link Repository} classes.
 */
@DataMongoTest
@TestPropertySource("/application-test.properties")
@Import(DiscoveryRestTemplateTestConfig.class)
public class EmbeddedMongoOrderRepositoryIT {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void saveOrderWhenReadBackShouldReturnSavedOrder() {
        Order order = Order.builder() //
            .type(new DrinkType("Latte", "Coffee")) //
            .size("Small") //
            .drinker("Dagbjørn") //
            .selectedOptions(new String[] { "skimmed milk" }) //
            .build();

        Order savedOrder = orderRepository.save(order);

        System.out.println(savedOrder);

        assertThat(savedOrder.getId()).isNotNull();

        Optional<Order> readbackOrder = orderRepository.findById(savedOrder.getId());

        System.out.println(readbackOrder.get());

        assertThat(readbackOrder.isPresent()).isTrue();
        assertThat(readbackOrder.get().toString()).isEqualTo(savedOrder.toString());
    }

    @Test
    public void findByIdWhenNoOrderShouldReturnNull() {
        Optional<Order> order = orderRepository.findById("123");

        System.err.println(order);

        assertThat(order.isPresent()).isFalse();
    }
}
