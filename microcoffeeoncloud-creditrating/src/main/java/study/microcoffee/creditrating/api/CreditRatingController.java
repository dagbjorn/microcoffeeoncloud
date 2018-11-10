package study.microcoffee.creditrating.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import study.microcoffee.creditrating.behavior.ServiceBehavior;
import study.microcoffee.creditrating.domain.CreditRating;

/**
 * Controller class of the Credit Rating REST API for checking if customers are creditworthy.
 * <p>
 * Also, some kind of configurable service behavior is executed to give the illusion of an unreliable backend service.
 * <p>
 * <b>TODO:</b> Currently a hardcoded credit rating of 70 is always returned. Needs to make it dependent on the actual customer.
 */
@RefreshScope
@RestController
@RequestMapping(path = "/coffeeshop", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
public class CreditRatingController {

    private Logger logger = LoggerFactory.getLogger(CreditRatingController.class);

    @Value("#{serviceBehaviorFactory.createServiceBehavior()}")
    public ServiceBehavior serviceBehavior;

    @GetMapping(path = "/creditrating/{customerId}")
    public CreditRating getCreditRating(@PathVariable("customerId") String customerId) {
        logger.debug("GET /creditrating/{}", customerId);

        // Execute some kind of service behavior.
        serviceBehavior.execute();

        // TODO Create some kind of database table where customers credit rating is found.
        CreditRating creditRating = new CreditRating(70);

        return creditRating;
    }
}
