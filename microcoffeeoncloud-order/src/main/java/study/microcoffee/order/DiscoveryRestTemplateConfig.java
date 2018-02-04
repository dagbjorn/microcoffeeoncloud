package study.microcoffee.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.cloud.netflix.ribbon.RibbonClientHttpRequestFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.netflix.client.http.HttpRequest;

/**
 * Discovery-supported RestTemplate configuration.
 */
@Configuration
@ConditionalOnClass(HttpRequest.class)
@ConditionalOnProperty(value = "ribbon.http.client.enabled", matchIfMissing = false)
public class DiscoveryRestTemplateConfig {

    private final Logger logger = LoggerFactory.getLogger(DiscoveryRestTemplateConfig.class);

    /**
     * Customizes the RestTemplate to use Ribbon load balancer to resolve service endpoints on format
     * <code>http://SERVICE_ID/path</code>. SERVICE_ID is identical to the Spring application name defined by
     * ${spring.application.name}.
     * <p>
     * Based on <a href=
     * "https://github.com/hotblac/spanners/blob/master/spanners-mvc/src/main/java/org/dontpanic/spanners/springmvc/config/RestClientConfig.java">
     * this source on GitHub</a>.
     */
    @Bean
    public RestTemplateCustomizer ribbonClientRestTemplateCustomizer(
        final RibbonClientHttpRequestFactory ribbonClientHttpRequestFactory) {
        return new RestTemplateCustomizer() {
            @Override
            public void customize(RestTemplate restTemplate) {
                logger.debug("RestTemplateCustomizer => requestFactory={}", ribbonClientHttpRequestFactory);

                restTemplate.setRequestFactory(ribbonClientHttpRequestFactory);
            }
        };
    }
}
