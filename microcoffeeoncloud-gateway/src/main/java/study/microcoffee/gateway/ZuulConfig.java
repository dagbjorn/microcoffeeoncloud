package study.microcoffee.gateway;

import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class of Zuul reverse proxy.
 */
@Configuration
@EnableZuulProxy
public class ZuulConfig {

}
