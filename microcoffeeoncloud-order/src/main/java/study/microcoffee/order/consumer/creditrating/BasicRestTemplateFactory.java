package study.microcoffee.order.consumer.creditrating;

import org.apache.hc.client5.http.classic.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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
public class BasicRestTemplateFactory {

    private final Logger logger = LoggerFactory.getLogger(BasicRestTemplateFactory.class);

    /**
     * Creates a RestTemplate instance that uses a {@link BufferingClientHttpRequestFactory}.
     */
    @Bean
    public RestTemplate basicRestTemplate(@Value("${app.creditrating.timeout}") int timeout) {
        logger.info("app.creditrating.timeout={}", timeout);

        HttpClient httpClient = HttpClientFactory.createDefaultClient(timeout);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));
        restTemplate.getInterceptors().add(new HttpLoggingInterceptor(false));
        return restTemplate;
    }
}
