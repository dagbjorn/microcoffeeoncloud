package study.microcoffee.order.consumer.creditrating;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import study.microcoffee.order.consumer.common.ConsumerBase;
import study.microcoffee.order.exception.ServiceCallFailedException;

/**
 * Basic implementation of WebClient-based REST CreditRatingConsumer.
 */
@Slf4j
@Component
@Qualifier(BasicWebClientCreditRatingConsumer.CONSUMER_TYPE)
public class BasicWebClientCreditRatingConsumer extends ConsumerBase implements CreditRatingConsumer {

    public static final String CONSUMER_TYPE = "basicWebClient";

    public static final String GET_CREDIT_RATING_RESOURCE = "/api/coffeeshop/creditrating/{customerId}";

    private WebClient webClient;

    private String baseUrl;

    public BasicWebClientCreditRatingConsumer(@Qualifier("discoveryWebClient") WebClient webClient,
        @Value("${app.creditrating.url}") String baseUrl) {

        this.webClient = webClient;
        this.baseUrl = baseUrl;

        log.info("app.creditrating.url={}", baseUrl);
    }

    @Override
    public int getCreditRating(String customerId) {
        String url = baseUrl + GET_CREDIT_RATING_RESOURCE;

        log.debug("GET request to {}, customerId={}", url, customerId);

        ResponseEntity<CreditRating> response = webClient.get() //
            .uri(url, customerId) //
            .accept(MediaType.APPLICATION_JSON) //
            .retrieve() //
            .toEntity(CreditRating.class) //
            .doOnError(e -> {
                throw new ServiceCallFailedException(e);
            }).block();

        log.debug("GET response from {}, response={}", url, response.getBody()); // NOSONAR Allow NPE

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return response.getBody().getRating(); // NOSONAR Allow NPE
        } else {
            throw new ServiceCallFailedException(response.getStatusCode() + " " + getReasonPhrase(response.getStatusCode()));
        }
    }
}
