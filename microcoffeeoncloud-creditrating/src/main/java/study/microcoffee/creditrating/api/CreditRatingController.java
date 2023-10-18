package study.microcoffee.creditrating.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import study.microcoffee.creditrating.SwaggerConfig;
import study.microcoffee.creditrating.behavior.ServiceBehavior;
import study.microcoffee.creditrating.domain.CreditRating;

/**
 * Controller class of the Credit Rating REST API for checking if customers are creditworthy.
 * <p>
 * Also, some kind of configurable service behavior is executed to give the illusion of an unreliable backend service.
 * <p>
 * <b>TODO:</b> Currently a hardcoded credit rating of 70 is always returned. Needs to make it dependent on the actual customer.
 */
@CrossOrigin( //
    origins = { //
        "https://localhost:8443" // Needed for Swagger from gateway (devlocal).
    })
@RefreshScope
@RestController
@RequestMapping(path = "/api/coffeeshop", produces = { MediaType.APPLICATION_JSON_VALUE })
@Tag(name = SwaggerConfig.CREDIT_RATING_TAG)
public class CreditRatingController {

    private Logger logger = LoggerFactory.getLogger(CreditRatingController.class);

    @Value("#{serviceBehaviorFactory.createServiceBehavior()}")
    public ServiceBehavior serviceBehavior;

    @GetMapping(path = "/creditrating/{customerId}")
    @GetCreditRatingSwagger
    public CreditRating getCreditRating(@PathVariable("customerId") String customerId) {
        logger.debug("GET /creditrating/{}", customerId);

        // Execute some kind of service behavior.
        serviceBehavior.execute();

        // TODO Create some kind of database table where customers credit rating is found.
        return new CreditRating(70);
    }
}
