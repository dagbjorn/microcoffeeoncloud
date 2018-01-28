package study.microcoffee.location.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;

/**
 * The Spring Boot application of the microservice.
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = { "study.microcoffee.location" })
public class Application {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);

        logger.debug("Running: {}", context.getDisplayName());
    }
}
