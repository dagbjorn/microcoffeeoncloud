package study.microcoffee.order.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import study.microcoffee.order.domain.Order;

/**
 * Interface to a Spring Data MongoDB repository for handling coffee orders. The implementation is generated automatically by
 * Spring.
 * <p>
 * Note that Spring generates a suite of standard repository methods including <code>findOne</code> which is a drop-in replacement
 * for <code>findById</code>. The latter is only specified for testing...
 */
public interface OrderRepository extends MongoRepository<Order, String> {

    /**
     * Find coffee order by ID.
     *
     * @param orderId
     *            the ID of the coffee order.
     * @return The Order object containing the requested coffee order if found; otherwise {@link Optional#empty()} if none was
     *         found.
     */
    Optional<Order> findById(String orderId);
}
