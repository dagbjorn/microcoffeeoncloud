package study.microcoffee.order;

import org.eclipse.jetty.client.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import study.microcoffee.order.common.logging.HttpLoggingInterceptor;
import study.microcoffee.order.common.logging.JettyHttpClientLogEnhancer;
import study.microcoffee.order.consumer.common.http.JettyHttpClientFactory;

/**
 * Discovery-supported REST consumer configuration.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "eureka.client.enabled", matchIfMissing = true)
public class DiscoveryConsumerConfig {

    @LoadBalanced
    @Bean
    public RestTemplate discoveryRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));
        restTemplate.getInterceptors().add(new HttpLoggingInterceptor(false));

        return restTemplate;
    }

    @Bean
    public WebClient discoveryWebClient(ReactorLoadBalancerExchangeFilterFunction lbFunction,
        OAuth2AuthorizedClientManager authorizedClientManager, @Value("${app.creditrating.timeout}") int timeout) {

        log.info("app.creditrating.timeout={}", timeout);

        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client = new ServletOAuth2AuthorizedClientExchangeFilterFunction(
            authorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId("order-service");

        HttpClient httpClient = JettyHttpClientFactory.createDefaultClient(timeout, new JettyHttpClientLogEnhancer(true));

        return WebClient.builder() //
            .filter(lbFunction) //
            .apply(oauth2Client.oauth2Configuration()) //
            .clientConnector(new JettyClientHttpConnector(httpClient)) //
            .build();
    }
}
