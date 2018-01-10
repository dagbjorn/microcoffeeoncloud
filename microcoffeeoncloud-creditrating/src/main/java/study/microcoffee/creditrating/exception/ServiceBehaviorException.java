package study.microcoffee.creditrating.exception;

/**
 * Exception that may be thrown by service behavior implementations.
 */
public class ServiceBehaviorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServiceBehaviorException() {
        super();
    }

    public ServiceBehaviorException(String message) {
        super(message);
    }

    public ServiceBehaviorException(Throwable cause) {
        super(cause);
    }

    public ServiceBehaviorException(String message, Throwable cause) {
        super(message, cause);
    }
}
