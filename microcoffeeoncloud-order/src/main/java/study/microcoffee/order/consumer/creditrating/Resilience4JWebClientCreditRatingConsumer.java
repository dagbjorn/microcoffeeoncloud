package study.microcoffee.order.consumer.creditrating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import io.github.resilience4j.retry.annotation.Retry;
import study.microcoffee.order.consumer.common.ConsumerBase;
import study.microcoffee.order.exception.ServiceCallFailedException;

/**
 * Resilience4J implementation of WebClient-based REST CreditRatingConsumer.
 */
@Component
@Qualifier("Resilience4JWebClient")
public class Resilience4JWebClientCreditRatingConsumer extends ConsumerBase implements CreditRatingConsumer {

    public static final String GET_CREDIT_RATING_RESOURCE = "/api/coffeeshop/creditrating/{customerId}";

    private final Logger logger = LoggerFactory.getLogger(Resilience4JWebClientCreditRatingConsumer.class);

    private WebClient webClient;

    private String baseUrl;

    public Resilience4JWebClientCreditRatingConsumer(@Qualifier("discoveryWebClient") WebClient webClient,
        @Value("${app.creditrating.url}") String baseUrl) {

        this.webClient = webClient;
        this.baseUrl = baseUrl;

        logger.info("app.creditrating.url={}", baseUrl);
    }

    @Override
    @Retry(name = "creditRating", fallbackMethod = "getMinimumCreditRating")
    public int getCreditRating(String customerId) {
        String url = baseUrl + GET_CREDIT_RATING_RESOURCE;

        logger.debug("GET request to {}, customerId={}", url, customerId);

        ResponseEntity<CreditRating> response = webClient.get() //
            .uri(url, customerId) //
            .accept(MediaType.APPLICATION_JSON) //
            .retrieve() //
            .toEntity(CreditRating.class) //
            .doOnError(e -> {
                throw new ServiceCallFailedException(e);
            }).block();

        logger.debug("GET response from {}, response={}", url, response.getBody()); // NOSONAR Allow NPE

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return response.getBody().getRating(); // NOSONAR Allow NPE
        } else {
            throw new ServiceCallFailedException(response.getStatusCode() + " " + getReasonPhrase(response.getStatusCode()));
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
