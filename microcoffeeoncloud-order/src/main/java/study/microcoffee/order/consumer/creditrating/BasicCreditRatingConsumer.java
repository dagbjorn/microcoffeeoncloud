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

import study.microcoffee.order.exception.ServiceCallFailedException;

/**
 * Basic implementation of REST-based CreditRatingConsumer.
 */
@Component
@Qualifier("Basic")
public class BasicCreditRatingConsumer implements CreditRatingConsumer {

    private static final String GET_CREDIT_RATING_RESOURCE = "/api/coffeeshop/creditrating/{customerId}";

    private final Logger logger = LoggerFactory.getLogger(BasicCreditRatingConsumer.class);

    private RestTemplate restTemplate;

    private String creditRatingEndpointUrl;

    public BasicCreditRatingConsumer(@Qualifier("creditRatingRestTemplate") RestTemplate restTemplate,
        @Value("${app.creditrating.url}") String endpointUrl) {
        this.restTemplate = restTemplate;
        this.creditRatingEndpointUrl = endpointUrl;

        logger.debug("restTemplate.requestFactory={}", restTemplate.getRequestFactory());
        logger.debug("app.creditrating.url={}", endpointUrl);
    }

    @Override
    public int getCreditRating(String customerId) {
        String url = creditRatingEndpointUrl + GET_CREDIT_RATING_RESOURCE;

        logger.debug("GET request to {}, customerId={}", url, customerId);

        try {
            ResponseEntity<CreditRating> response = restTemplate.getForEntity(url, CreditRating.class, customerId);

            logger.debug("GET response from {}, response={}", url, response.getBody());

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return response.getBody().getRating();
            } else {
                throw new ServiceCallFailedException(response.getStatusCode() + " " + response.getStatusCode().getReasonPhrase());
            }
        } catch (RestClientException e) {
            throw new ServiceCallFailedException(e);
        }
    }
}
