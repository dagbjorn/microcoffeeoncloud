package study.microcoffee.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.repository.Repository;
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
public class EmbeddedMongoOrderRepositoryIT {

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
}
