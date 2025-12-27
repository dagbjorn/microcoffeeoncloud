package study.microcoffee.order.consumer.creditrating;

import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestClient;

import study.microcoffee.order.common.logging.HttpLoggingInterceptor;
import study.microcoffee.order.consumer.common.http.HttpClientFactory;
import study.microcoffee.order.consumer.common.oauth2.OAuth2TokenInterceptor;

/**
 * Factory class to create Spring RestClient.Builder instances.
 * <p>
 * The reason of creating RestClient.Builder instances, and not RestClient instances, is because of the {@link LoadBalanced}
 * annotation that requires a Builder instance.
 */
@Configuration
public class CreditRatingRestClientFactory {

    // Defining this bean as @Primary fixes a Spring autowiring issue (two beans found). Ref:
    // https://docs.spring.io/spring-cloud-commons/reference/spring-cloud-commons/common-abstractions.html#multiple-restclient-objects
    @Primary
    @Bean
    public RestClient.Builder basicRestClientBuilder(OAuth2AuthorizedClientManager authorizedClientManager,
        ClientRegistrationRepository clientRegistrationRepository, @Value("${app.creditrating.timeout}") int timeout) {

        return createRestClient(authorizedClientManager, clientRegistrationRepository, timeout, true);
    }

    @LoadBalanced
    @Bean
    @ConditionalOnProperty(value = "eureka.client.enabled", matchIfMissing = true)
    public RestClient.Builder discoveryRestClientBuilder(OAuth2AuthorizedClientManager authorizedClientManager,
        ClientRegistrationRepository clientRegistrationRepository, @Value("${app.creditrating.timeout}") int timeout) {

        return createRestClient(authorizedClientManager, clientRegistrationRepository, timeout, true);
    }

    private RestClient.Builder createRestClient(OAuth2AuthorizedClientManager authorizedClientManager,
        ClientRegistrationRepository clientRegistrationRepository, int timeout, boolean bodyLogging) {

        HttpClient httpClient = HttpClientFactory.createDefaultClient(timeout);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("order-service");

        return RestClient.builder() //
            .bufferContent((_, _) -> true) //
            .requestFactory(requestFactory) //
            .requestInterceptor(new HttpLoggingInterceptor(true, bodyLogging)) //
            .requestInterceptor(new OAuth2TokenInterceptor(authorizedClientManager, clientRegistration));
    }
}
