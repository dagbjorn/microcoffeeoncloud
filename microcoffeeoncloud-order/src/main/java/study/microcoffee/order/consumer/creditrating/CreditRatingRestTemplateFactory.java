package study.microcoffee.order.consumer.creditrating;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import study.microcoffee.order.common.logging.HttpLoggingInterceptor;
import study.microcoffee.order.consumer.common.http.HttpClientFactory;

/**
 * Factory class to create Spring RestTemplate instances.
 * <p>
 * Note! RestTemplate instances are supposed to be thread-safe.
 */
@Component
public class CreditRatingRestTemplateFactory {

    private final Logger logger = LoggerFactory.getLogger(CreditRatingRestTemplateFactory.class);

    @Value("${app.creditrating.timeout}")
    private int timeout;

    /**
     * Creates a RestTemplate instance that uses a {@link BufferingClientHttpRequestFactory}.
     *
     * @return The RestTemplate instance.
     */
    public RestTemplate createRestTemplate() {
        logger.info("app.creditrating.timeout={}", timeout);

        HttpClient httpClient = HttpClientFactory.createDefaultClient(timeout);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));
        restTemplate.getInterceptors().add(new HttpLoggingInterceptor(false));
        return restTemplate;
    }
}
