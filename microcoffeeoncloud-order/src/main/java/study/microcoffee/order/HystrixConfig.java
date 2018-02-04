package study.microcoffee.order;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class of Hystrix.
 */
@Configuration
@EnableCircuitBreaker
@EnableHystrixDashboard
public class HystrixConfig {

}
