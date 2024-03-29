package study.microcoffee.creditrating.behavior;

import java.security.SecureRandom;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import study.microcoffee.creditrating.exception.ServiceBehaviorException;

/**
 * Class that implements unstable service behavior determined by a random value.
 */
public class UnstableServiceBehavior extends AbstractServiceBehavior {

    private final Logger logger = LoggerFactory.getLogger(UnstableServiceBehavior.class);

    private Random random = new SecureRandom();

    private int delaySecs;

    public UnstableServiceBehavior(int delaySecs) {
        this.delaySecs = delaySecs;
    }

    @Override
    public void execute() {
        int value = random.nextInt(10);

        switch (value) {
            case 0, 1, 2:
                sleep(DEFAULT_EXECUTION_TIME_MS);
                logger.debug("Unstable behavior => success...");
                break;

            case 3, 4, 5, 6:
                sleep(DEFAULT_EXECUTION_TIME_MS);
                logger.debug("Unstable behavior => failed...");

                throw new ServiceBehaviorException("Backend service failed");

            case 7, 8, 9:
                logger.debug("Unstable behavior => slow...");
                sleep(delaySecs * 1000);
                break;

            default:
                break;
        }
    }
}
