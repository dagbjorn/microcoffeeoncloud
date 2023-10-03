package study.microcoffee.order.consumer.creditrating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.retry.annotation.Retry;
import study.microcoffee.order.consumer.common.ConsumerBase;
import study.microcoffee.order.exception.ServiceCallFailedException;

/**
 * Resilience4J implementation of REST-based CreditRatingConsumer.
 */
@Component
@Qualifier("Resilience4J")
public class Resilience4JCreditRatingConsumer extends ConsumerBase implements CreditRatingConsumer {

    private static final String GET_CREDIT_RATING_RESOURCE = "/api/coffeeshop/creditrating/{customerId}";

    private final Logger logger = LoggerFactory.getLogger(Resilience4JCreditRatingConsumer.class);

    private RestTemplate restTemplate;

    private String creditRatingEndpointUrl;

    public Resilience4JCreditRatingConsumer(@Qualifier("discoveryRestTemplate") RestTemplate restTemplate,
        @Value("${app.creditrating.url}") String endpointUrl) {
        this.restTemplate = restTemplate;
        this.creditRatingEndpointUrl = endpointUrl;

        logger.debug("restTemplate.requestFactory={}", restTemplate.getRequestFactory());
        logger.debug("app.creditrating.url={}", endpointUrl);
    }

    @Override
    @Retry(name = "creditRating", fallbackMethod = "getMinimumCreditRating")
    public int getCreditRating(String customerId) {
        String url = creditRatingEndpointUrl + GET_CREDIT_RATING_RESOURCE;

        logger.debug("GET request to {}, customerId={}", url, customerId);

        try {
            ResponseEntity<CreditRating> response = restTemplate.getForEntity(url, CreditRating.class, customerId);

            logger.debug("GET response from {}, response={}", url, response.getBody());

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return response.getBody().getRating(); // NOSONAR Allow NPE
            } else {
                throw new ServiceCallFailedException(response.getStatusCode() + " " + getReasonPhrase(response.getStatusCode()));
            }
        } catch (RestClientException e) {
            throw new ServiceCallFailedException(e);
        }
    }

    /**
     * Fallback method that returns the minimum credit rating.
     *
     * @param customerId
     *            the customer ID.
     * @return The minimum credit rating.
     */
    public int getMinimumCreditRating(String customerId, Exception e) {
        logger.debug("Fallback method getMinimumCreditRating called => credit rating 0", e);

        return 0;
    }
}
