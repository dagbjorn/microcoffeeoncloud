package study.microcoffee.gateway.logging;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility providing a sequence number for log record tagging.
 */
public class LoggingSequence {

    private static AtomicInteger currentSequence = new AtomicInteger();

    private LoggingSequence() {
    }

    /**
     * @return Next sequence number.
     */
    public static int getNextSequence() {
        return currentSequence.incrementAndGet();
    }
}
