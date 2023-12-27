package study.microcoffee.order.consumer.creditrating;

import org.apache.hc.client5.http.classic.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import study.microcoffee.order.common.logging.HttpLoggingInterceptor;
import study.microcoffee.order.consumer.common.http.HttpClientFactory;
import study.microcoffee.order.consumer.common.oauth2.OAuth2TokenInterceptor;

/**
 * Factory class to create Spring RestTemplate instances.
 * <p>
 * Note! RestTemplate instances are supposed to be thread-safe.
 */
@Component
public class CreditRatingRestTemplateFactory {

    private final Logger logger = LoggerFactory.getLogger(CreditRatingRestTemplateFactory.class);

    /**
     * Creates a RestTemplate instance that uses a {@link BufferingClientHttpRequestFactory}.
     */
    @Bean
    public RestTemplate basicRestTemplate(OAuth2AuthorizedClientManager authorizedClientManager,
        ClientRegistrationRepository clientRegistrationRepository, @Value("${app.creditrating.timeout}") int timeout) {

        return createRestTemplate(authorizedClientManager, clientRegistrationRepository, timeout);
    }

    @LoadBalanced
    @Bean
    @ConditionalOnProperty(value = "eureka.client.enabled", matchIfMissing = true)
    public RestTemplate discoveryRestTemplate(OAuth2AuthorizedClientManager authorizedClientManager,
        ClientRegistrationRepository clientRegistrationRepository, @Value("${app.creditrating.timeout}") int timeout) {

        return createRestTemplate(authorizedClientManager, clientRegistrationRepository, timeout);
    }

    private RestTemplate createRestTemplate(OAuth2AuthorizedClientManager authorizedClientManager,
        ClientRegistrationRepository clientRegistrationRepository, int timeout) {

        logger.info("app.creditrating.timeout={}", timeout);

        HttpClient httpClient = HttpClientFactory.createDefaultClient(timeout);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("order-service");

        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));
        restTemplate.getInterceptors().add(new HttpLoggingInterceptor(true));
        restTemplate.getInterceptors().add(new OAuth2TokenInterceptor(authorizedClientManager, clientRegistration));
        return restTemplate;
    }
}
