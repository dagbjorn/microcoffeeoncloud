package study.microcoffee.creditrating.behavior;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * Refreshable service behavior configuration.
 */
@RefreshScope
@Component
public class ServiceBehaviorFactory {

    private static Logger logger = LoggerFactory.getLogger(ServiceBehaviorFactory.class);

    @Value("${creditrating.service.behavior}")
    private int serviceBehavior;

    @Value("${creditrating.service.behavior.delay}")
    private int delay;

    public ServiceBehavior createServiceBehavior() {

        logger.debug("Creating CreditRating service behavior={}, delay={}", serviceBehavior, delay);

        switch (serviceBehavior) {
            case 0:
                return new StableServiceBehavior();

            case 1:
                return new FailingServiceBehavior();

            case 2:
                return new SlowServiceBehavior(delay);

            case 3:
                return new UnstableServiceBehavior(delay);

            default:
                return new StableServiceBehavior();
        }
    }
}
