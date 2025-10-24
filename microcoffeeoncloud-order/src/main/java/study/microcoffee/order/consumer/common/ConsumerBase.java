package study.microcoffee.order.consumer.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/**
 * Superclass containing basic consumer functionality.
 */
public class ConsumerBase {

    /**
     * Returns the reason phrase of a HTTP status code.
     *
     * @param statusCode
     *            the HTTP status code.
     * @return The reason phrase.
     */
    public String getReasonPhrase(HttpStatusCode statusCode) {
        try {
            return HttpStatus.valueOf(statusCode.value()).getReasonPhrase();
        } catch (Exception _) {
            return "";
        }
    }
}
