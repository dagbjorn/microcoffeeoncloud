package study.microcoffee.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.repository.Repository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import study.microcoffee.order.domain.DrinkType;
import study.microcoffee.order.domain.Order;

/**
 * Integration tests of {@link OrderRepository} that uses an auto-configured Embedded MongoDB database.
 * <p>
 * The @DataMongoTest annotation will scan for @Document classes and Spring {@link Repository} classes.
 */
@RunWith(SpringRunner.class)
@DataMongoTest
@DirtiesContext
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
