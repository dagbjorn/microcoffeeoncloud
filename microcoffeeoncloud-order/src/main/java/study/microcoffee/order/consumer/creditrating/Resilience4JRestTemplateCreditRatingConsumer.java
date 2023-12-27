package study.microcoffee.order.consumer.creditrating;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

/**
 * Resilience4J implementation of REST-based CreditRatingConsumer.
 */
@Slf4j
@Component
@Qualifier(Resilience4JRestTemplateCreditRatingConsumer.CONSUMER_TYPE)
public class Resilience4JRestTemplateCreditRatingConsumer extends BasicRestTemplateCreditRatingConsumer {

    public static final String CONSUMER_TYPE = "resilience4JRestTemplate";

    public Resilience4JRestTemplateCreditRatingConsumer(@Qualifier("discoveryRestTemplate") RestTemplate restTemplate,
        @Value("${app.creditrating.url}") String endpointUrl) {
        super(restTemplate, endpointUrl);
    }

    @Override
    @Retry(name = "creditRating", fallbackMethod = "getMinimumCreditRating")
    public int getCreditRating(String customerId) {
        return super.getCreditRating(customerId);
    }

    /**
     * Fallback method that returns the minimum credit rating.
     *
     * @param customerId
     *            the customer ID.
     * @return The minimum credit rating.
     */
    public int getMinimumCreditRating(String customerId, Exception e) {
        log.debug("Fallback method getMinimumCreditRating called => credit rating 0", e);

        return 0;
    }
}
