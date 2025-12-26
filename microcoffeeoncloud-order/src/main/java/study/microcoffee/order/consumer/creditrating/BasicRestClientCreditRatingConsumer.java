package study.microcoffee.order.consumer.creditrating;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;
import study.microcoffee.order.consumer.common.ConsumerBase;
import study.microcoffee.order.exception.ServiceCallFailedException;

/**
 * Basic implementation of RestClient-based REST CreditRatingConsumer.
 */
@Slf4j
@Component
@Qualifier(BasicRestClientCreditRatingConsumer.CONSUMER_TYPE)
public class BasicRestClientCreditRatingConsumer extends ConsumerBase implements CreditRatingConsumer {

    public static final String CONSUMER_TYPE = "basicRestClient";

    public static final String GET_CREDIT_RATING_RESOURCE = "/api/coffeeshop/creditrating/{customerId}";

    private RestClient restClient;

    private String baseUrl;

    public BasicRestClientCreditRatingConsumer(@Qualifier("discoveryRestClient") RestClient.Builder restClientBuilder,
        @Value("${app.creditrating.url}") String baseUrl) {

        this.restClient = restClientBuilder.build();
        this.baseUrl = baseUrl;

        log.info("app.creditrating.url={}", baseUrl);
    }

    @Override
    public int getCreditRating(String customerId) {
        String url = baseUrl + GET_CREDIT_RATING_RESOURCE;

        log.debug("GET request to {}, customerId={}", url, customerId);

        ResponseEntity<CreditRating> response = restClient.get() //
            .uri(url, customerId) //
            .accept(MediaType.APPLICATION_JSON) //
            .retrieve() //
            .onStatus(status -> status != HttpStatus.OK, (req, resp) -> {
                throw new ServiceCallFailedException(resp.getStatusCode() + " " + getReasonPhrase(resp.getStatusCode()));
            }) //
            .toEntity(CreditRating.class);

        log.debug("GET response from {}, response={}", url, response.getBody()); // NOSONAR Allow NPE

        return response.getBody().getRating(); // NOSONAR Allow NPE
    }
}
