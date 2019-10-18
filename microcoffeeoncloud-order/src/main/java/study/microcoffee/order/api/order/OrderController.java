package study.microcoffee.order.api.order;

import java.net.URI;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import study.microcoffee.order.api.order.model.OrderModel;
import study.microcoffee.order.consumer.creditrating.CreditRatingConsumer;
import study.microcoffee.order.domain.Order;
import study.microcoffee.order.exception.OrderNotFoundException;
import study.microcoffee.order.repository.OrderRepository;

/**
 * Controller class of the Order REST API for handling coffee orders.
 */
@RestController
@RequestMapping(path = "/api/coffeeshop", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

    static final String BASIC_CONSUMER = "Basic";
    static final String HYSTRIX_CONSUMER = "Hystrix";
    static final String CREDIT_RATING_CONSUMER = HYSTRIX_CONSUMER;

    private static final int MINIMUM_CREDIT_RATING = 50;

    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    @Qualifier(CREDIT_RATING_CONSUMER)
    private CreditRatingConsumer creditRatingConsumer;

    private ModelMapper modelMapper = new ModelMapper();

    /**
     * Saves the order in the database.
     * <p>
     * If the order is successfully created, HTTP status 201 (Created) is returned.
     *
     * @param coffeeShopId
     *            the ID of the coffee shop.
     * @param orderModel
     *            the JSON-formatted coffee order received in the HTTP request body.
     * @return A ResponseEntity object containing 1) the saved coffee order including its ID, and 2) a HTTP location header
     *         containing the URL for reading the saved order.
     */
    @PostMapping(path = "/{coffeeShopId}/order", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderModel> saveOrder(@PathVariable("coffeeShopId") long coffeeShopId, @RequestBody OrderModel orderModel) {
        logger.debug("POST /{}/order body={}", coffeeShopId, orderModel);

        Order order = convertToEntity(orderModel);

        int creditRating = creditRatingConsumer.getCreditRating(order.getDrinker());
        if (creditRating < MINIMUM_CREDIT_RATING) {
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED) //
                .contentType(MediaType.TEXT_PLAIN) // To avoid "XML Parsing Error: no element found" in Firefox.
                .build();
        }

        order.setCoffeeShopId(coffeeShopId);

        order = orderRepository.save(order);

        // Create URL of Location header
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{orderId}").buildAndExpand(order.getId()).toUri();

        return ResponseEntity.created(location).body(convertToModel(order));
    }

    /**
     * Reads a coffee order by ID.
     * <p>
     * On success, HTTP status 200 (OK) is returned.
     *
     * @param coffeeShopId
     *            the ID of the coffee shop.
     * @param orderId
     *            the ID of the order to read.
     * @return The requested order if found.
     * @throws OrderNotFoundException
     *             if no such order ID exists. The exception class is mapped to HTTP status 204 (No content).
     */
    @GetMapping(path = "/{coffeeShopId}/order/{orderId}")
    public OrderModel getOrder(@PathVariable("coffeeShopId") long coffeeShopId, @PathVariable("orderId") String orderId) {
        logger.debug("GET /{}/order/{}", coffeeShopId, orderId);

        Optional<Order> order = orderRepository.findById(orderId);
        if (!order.isPresent()) {
            throw new OrderNotFoundException(orderId);
        }

        return convertToModel(order.get());
    }

    private Order convertToEntity(OrderModel orderModel) {
        return modelMapper.map(orderModel, Order.class);
    }

    private OrderModel convertToModel(Order order) {
        return modelMapper.map(order, OrderModel.class);
    }
}
