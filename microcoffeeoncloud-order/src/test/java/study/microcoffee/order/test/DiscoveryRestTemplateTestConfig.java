package study.microcoffee.order.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Test configuration class for Discovery RestTemplate.
 */
@TestConfiguration
public class DiscoveryRestTemplateTestConfig {

    @Bean
    public RestTemplate discoveryRestTemplate() {
        return new RestTemplate();
    }
}
