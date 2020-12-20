package study.microcoffee.order;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Discovery-supported RestTemplate configuration.
 */
@Configuration
@ConditionalOnProperty(value = "eureka.client.enabled", matchIfMissing = true)
public class DiscoveryRestTemplateConfig {

    @LoadBalanced
    @Bean
    public RestTemplate discoveryRestTemplate() {
        return new RestTemplate();
    }
}
