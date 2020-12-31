package study.microcoffee.order.consumer.creditrating;

import org.eclipse.jetty.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.JettyClientHttpConnector;
import org.springframework.http.client.reactive.JettyResourceFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import study.microcoffee.order.consumer.common.http.JettyHttpClientFactory;

/**
 * Factory class to create Spring WebClient instances.
 */
@Component
public class BasicWebClientFactory {

    private final Logger logger = LoggerFactory.getLogger(BasicWebClientFactory.class);

    @Bean
    public WebClient basicWebClient(JettyResourceFactory resourceFactory, @Value("${app.creditrating.timeout}") int timeout) {
        logger.info("app.creditrating.timeout={}", timeout);

        HttpClient httpClient = JettyHttpClientFactory.createDefaultClient(timeout);

        return WebClient.builder() //
            .clientConnector(new JettyClientHttpConnector(httpClient, resourceFactory)) //
            .build();
    }
}
