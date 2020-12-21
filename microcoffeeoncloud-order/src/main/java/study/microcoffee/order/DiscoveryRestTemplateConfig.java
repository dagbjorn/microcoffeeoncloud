package study.microcoffee.order;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import study.microcoffee.order.common.logging.HttpLoggingInterceptor;

/**
 * Discovery-supported RestTemplate configuration.
 */
@Configuration
@ConditionalOnProperty(value = "eureka.client.enabled", matchIfMissing = true)
public class DiscoveryRestTemplateConfig {

    @LoadBalanced
    @Bean
    public RestTemplate discoveryRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));
        restTemplate.getInterceptors().add(new HttpLoggingInterceptor(false));

        return restTemplate;
    }
}
