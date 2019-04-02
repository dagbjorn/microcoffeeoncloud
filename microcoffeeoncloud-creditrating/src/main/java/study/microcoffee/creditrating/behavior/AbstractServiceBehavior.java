package study.microcoffee.creditrating.behavior;

/**
 * Abstract class containing basic service behavior.
 */
public abstract class AbstractServiceBehavior implements ServiceBehavior {

    /** Default execution time in millisecs before return on success or failure. */
    protected static final int DEFAULT_EXECUTION_TIME_MS = 300;

    /**
     * Delays caller for the given number of millisecs.
     *
     * @param delayMillisecs
     *            the delay in millisecs.
     */
    protected void sleep(int delayMillisecs) {
        try {
            Thread.sleep(delayMillisecs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
