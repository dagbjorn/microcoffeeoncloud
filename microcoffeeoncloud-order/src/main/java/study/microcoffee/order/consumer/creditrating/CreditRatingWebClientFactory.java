package study.microcoffee.order.consumer.creditrating;

import org.eclipse.jetty.client.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.http.client.reactive.JettyResourceFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import study.microcoffee.order.common.logging.JettyHttpClientLogEnhancer;
import study.microcoffee.order.consumer.common.http.JettyHttpClientFactory;

/**
 * Factory class to create Spring WebClient instances.
 */
@Component
public class CreditRatingWebClientFactory {

    @Bean
    public WebClient basicWebClient(JettyResourceFactory resourceFactory, OAuth2AuthorizedClientManager authorizedClientManager,
        @Value("${app.creditrating.timeout}") int timeout) {

        ServletOAuth2AuthorizedClientExchangeFilterFunction authorizedClientFilterFunction = createAuthorizedClientFilterFunction(
            authorizedClientManager);

        return WebClient.builder() //
            .apply(authorizedClientFilterFunction.oauth2Configuration()) //
            .clientConnector(new JettyClientHttpConnector(createHttpClient(timeout), resourceFactory)) //
            .build();
    }

    @Bean
    @ConditionalOnProperty(value = "eureka.client.enabled", matchIfMissing = true)
    public WebClient discoveryWebClient(ReactorLoadBalancerExchangeFilterFunction loadBalancerFunction,
        OAuth2AuthorizedClientManager authorizedClientManager, @Value("${app.creditrating.timeout}") int timeout) {

        ServletOAuth2AuthorizedClientExchangeFilterFunction authorizedClientFilterFunction = createAuthorizedClientFilterFunction(
            authorizedClientManager);

        return WebClient.builder() //
            .filter(loadBalancerFunction) //
            .apply(authorizedClientFilterFunction.oauth2Configuration()) //
            .clientConnector(new JettyClientHttpConnector(createHttpClient(timeout))) //
            .build();
    }

    private ServletOAuth2AuthorizedClientExchangeFilterFunction createAuthorizedClientFilterFunction(
        OAuth2AuthorizedClientManager authorizedClientManager) {

        ServletOAuth2AuthorizedClientExchangeFilterFunction authorizedClientFilterFunction = new ServletOAuth2AuthorizedClientExchangeFilterFunction(
            authorizedClientManager);
        authorizedClientFilterFunction.setDefaultClientRegistrationId("order-service");

        return authorizedClientFilterFunction;
    }

    private HttpClient createHttpClient(int timeout) {
        return JettyHttpClientFactory.createDefaultClient(timeout, new JettyHttpClientLogEnhancer(true));
    }
}
