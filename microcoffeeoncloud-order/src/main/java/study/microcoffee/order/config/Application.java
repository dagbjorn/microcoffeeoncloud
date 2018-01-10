package study.microcoffee.order.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * The Spring Boot application of the microservice.
 */
@SpringBootApplication(scanBasePackages = { "study.microcoffee.order" })
@EnableMongoRepositories(basePackages = "study.microcoffee.order.repository")
@EnableCircuitBreaker
@EnableHystrixDashboard
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);

        logger.debug("Running: {}", context.getDisplayName());
    }
}
