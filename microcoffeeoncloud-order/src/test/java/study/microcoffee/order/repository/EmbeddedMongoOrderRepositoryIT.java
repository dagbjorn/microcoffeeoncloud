package study.microcoffee.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.Repository;
import org.springframework.test.context.TestPropertySource;

import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import de.flapdoodle.reverse.transitions.Start;
import study.microcoffee.order.domain.DrinkType;
import study.microcoffee.order.domain.Order;
import study.microcoffee.order.test.DiscoveryTestConfig;

/**
 * Integration tests of {@link OrderRepository} that uses an auto-configured Embedded MongoDB database.
 * <p>
 * The @DataMongoTest annotation will scan for @Document classes and Spring {@link Repository} classes.
 *
 * @implNote DataMongoTest doesn't autoconfigure and start Embedded MongoDB with Spring Boot 4 and
 *           de.flapdoodle.embed.mongo.spring4x, hence we start MongoDB manually running on a fixed port.
 */
@DataMongoTest(properties = {
    "spring.mongodb.uri=mongodb://localhost:" + EmbeddedMongoOrderRepositoryIT.MONGODB_PORT + "/microcoffee?ssl=false" })
@TestPropertySource("/application-test.properties")
@Import(DiscoveryTestConfig.class)
class EmbeddedMongoOrderRepositoryIT {

    static final int MONGODB_PORT = 28017;
    static final Version.Main MONGODB_VERSION = Version.Main.V8_2;

    private static TransitionWalker.ReachedState<RunningMongodProcess> running;

    @Autowired
    private OrderRepository orderRepository;

    // Ref. https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo/blob/main/docs/Howto.md
    @BeforeAll
    static void startMongodb() {
        Mongod mongod = Mongod.builder() //
            .net(Start.to(Net.class).initializedWith(Net.defaults().withPort(MONGODB_PORT))) //
            .build();

        running = mongod.start(MONGODB_VERSION);
    }

    @AfterAll
    static void teardownMongodb() {
        if (running != null) {
            running.close();
        }
        running = null;
    }

    @Test
    void saveOrderWhenReadBackShouldReturnSavedOrder() {
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

        assertThat(readbackOrder).isPresent();
        assertThat(readbackOrder.get()).hasToString(savedOrder.toString());
    }

    @Test
    void findByIdWhenNoOrderShouldReturnNull() {
        Optional<Order> order = orderRepository.findById("123");

        System.err.println(order);

        assertThat(order).isNotPresent();
    }
}
