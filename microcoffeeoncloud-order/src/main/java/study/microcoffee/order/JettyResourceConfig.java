package study.microcoffee.order;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.JettyResourceFactory;

/**
 * Configuration class of Jetty resources.
 */
@Configuration
public class JettyResourceConfig {

    /**
     * Create factory to manage Jetty resources within the lifecycle of a Spring ApplicationContext.
     */
    @Bean
    public JettyResourceFactory jettyResourceFactory() {
        return new JettyResourceFactory();
    }
}
