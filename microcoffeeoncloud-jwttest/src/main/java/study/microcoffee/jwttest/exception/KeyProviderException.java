package study.microcoffee.jwttest.exception;

/**
 * Exception thrown on errors related to fetching or processing of RSA keys.
 */
public class KeyProviderException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public KeyProviderException(String message) {
        super(message);
    }

    public KeyProviderException(Throwable cause) {
        super(cause.toString(), cause);
    }

    public KeyProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
