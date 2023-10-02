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

import study.microcoffee.order.consumer.common.ConsumerBase;
import study.microcoffee.order.exception.ServiceCallFailedException;

/**
 * Basic implementation of WebClient-based REST CreditRatingConsumer.
 */
@Component
@Qualifier("BasicWebClient")
public class BasicWebClientCreditRatingConsumer extends ConsumerBase implements CreditRatingConsumer {

    public static final String GET_CREDIT_RATING_RESOURCE = "/api/coffeeshop/creditrating/{customerId}";

    private final Logger logger = LoggerFactory.getLogger(BasicWebClientCreditRatingConsumer.class);

    private WebClient webClient;

    private String baseUrl;

    public BasicWebClientCreditRatingConsumer(@Qualifier("basicWebClient") WebClient webClient,
        @Value("${app.creditrating.url}") String baseUrl) {

        this.webClient = webClient;
        this.baseUrl = baseUrl;

        logger.info("app.creditrating.url={}", baseUrl);
    }

    @Override
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
}
