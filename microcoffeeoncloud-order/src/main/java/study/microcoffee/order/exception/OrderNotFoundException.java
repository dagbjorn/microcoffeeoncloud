package study.microcoffee.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to throw if requested order is not found. The exception class is mapped to HTTP status 204 (No content).
 */
@ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "Requested order ID is not found")
public class OrderNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OrderNotFoundException(String orderId) {
        super(String.format("Order with id %s is not found", orderId));
    }
}
