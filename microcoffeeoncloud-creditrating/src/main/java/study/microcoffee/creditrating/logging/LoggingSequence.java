package study.microcoffee.creditrating.logging;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility providing a sequence number for log record tagging.
 */
public class LoggingSequence {

    private static AtomicInteger currentSequence = new AtomicInteger();

    /**
     * @return Next sequence number.
     */
    public static int getNextSequence() {
        return currentSequence.incrementAndGet();
    }
}
