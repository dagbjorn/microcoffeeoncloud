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

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import study.microcoffee.order.exception.ServiceCallFailedException;

/**
 * Hystrix-augmented implementation of REST-based CreditRatingConsumer.
 */
@Component
@Qualifier("Hystrix")
public class HystrixCreditRatingConsumer implements CreditRatingConsumer {

    private static final String GET_CREDIT_RATING_RESOURCE = "/coffeeshop/creditrating/{customerId}";

    private final Logger logger = LoggerFactory.getLogger(HystrixCreditRatingConsumer.class);

    @Value("#{creditRatingRestTemplateFactory.createRestTemplate()}")
    private RestTemplate restTemplate;

    @Value("${creditrating.endpointurl}")
    private String creditRatingEndpointUrl;

    @HystrixCommand(fallbackMethod = "getMinimumCreditRating", commandKey = "getCreditRating", commandProperties = {})
    @Override
    public int getCreditRating(String customerId) {
        String url = creditRatingEndpointUrl + GET_CREDIT_RATING_RESOURCE;

        logger.debug("GET request to {}, customerId={}", url, customerId);

        try {
            ResponseEntity<CreditRating> response = restTemplate.getForEntity(url, CreditRating.class, customerId);

            logger.debug("GET response from {}, response={}", url, response.getBody());

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return response.getBody().getCreditRating();
            } else {
                throw new ServiceCallFailedException(response.getStatusCode() + " " + response.getStatusCode().getReasonPhrase());
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
    public int getMinimumCreditRating(String customerId) {
        logger.debug("Fallback method getMinimumCreditRating called => credit rating 0");

        return 0;
    }
}
