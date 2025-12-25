package study.microcoffee.order.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Test configuration class for stubbed Discovery functionality.
 */
@TestConfiguration
public class DiscoveryTestConfig {

    @Bean
    public RestTemplate discoveryRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RestClient discoveryRestClient() {
        return RestClient.builder().build();
    }

    @Bean
    public WebClient discoveryWebClient() {
        return WebClient.create();
    }
}
