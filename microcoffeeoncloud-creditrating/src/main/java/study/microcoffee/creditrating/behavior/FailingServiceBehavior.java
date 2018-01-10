package study.microcoffee.creditrating.behavior;

import study.microcoffee.creditrating.exception.ServiceBehaviorException;

/**
 * Class that implements failing service behavior.
 */
public class FailingServiceBehavior extends AbstractServiceBehavior {

    @Override
    public void execute() {
        sleep(DEFAULT_EXECUTION_TIME_MS);

        throw new ServiceBehaviorException("Backend service failed");
    }
}
