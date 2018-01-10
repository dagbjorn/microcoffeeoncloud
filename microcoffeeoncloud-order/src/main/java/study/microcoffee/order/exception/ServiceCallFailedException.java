package study.microcoffee.order.exception;

/**
 * Exception thrown if a downstream service call failed.
 */
public class ServiceCallFailedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServiceCallFailedException() {
        super();
    }

    public ServiceCallFailedException(String message) {
        super(message);
    }

    public ServiceCallFailedException(Throwable cause) {
        super(cause);
    }

    public ServiceCallFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
