package study.microcoffee.order.consumer.creditrating;

import org.apache.hc.client5.http.classic.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import study.microcoffee.order.common.logging.HttpLoggingInterceptor;
import study.microcoffee.order.consumer.common.http.HttpClientFactory;

/**
 * Factory class to create Spring RestClient instances.
 */
@Component
public class BasicRestClientFactory {

    private final Logger logger = LoggerFactory.getLogger(BasicRestClientFactory.class);

    /**
     * Creates a RestClient.Builder instance that uses a {@link BufferingClientHttpRequestFactory}.
     */
    @Bean
    public RestClient.Builder basicRestClientBuilder(@Value("${app.creditrating.timeout}") int timeout) {
        logger.info("app.creditrating.timeout={}", timeout);

        HttpClient httpClient = HttpClientFactory.createDefaultClient(timeout);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return RestClient.builder() //
            .requestFactory(new BufferingClientHttpRequestFactory(requestFactory)) //
            .requestInterceptor(new HttpLoggingInterceptor(false));
    }
}
