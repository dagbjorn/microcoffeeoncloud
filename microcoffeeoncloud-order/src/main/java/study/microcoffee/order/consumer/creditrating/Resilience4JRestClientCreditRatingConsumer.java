package study.microcoffee.order.consumer.creditrating;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

/**
 * Resilience4J implementation of RestClient-based REST CreditRatingConsumer.
 */
@Slf4j
@Component
@Qualifier(Resilience4JRestClientCreditRatingConsumer.CONSUMER_TYPE)
public class Resilience4JRestClientCreditRatingConsumer extends BasicRestClientCreditRatingConsumer {

    public static final String CONSUMER_TYPE = "resilience4JRestClient";

    public Resilience4JRestClientCreditRatingConsumer(@Qualifier("discoveryRestClient") RestClient.Builder restClientBuilder,
        @Value("${app.creditrating.url}") String baseUrl) {
        super(restClientBuilder, baseUrl);
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
