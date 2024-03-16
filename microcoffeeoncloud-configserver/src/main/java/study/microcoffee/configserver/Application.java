package study.microcoffee.configserver;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * The Spring Boot application of the microservice.
 */
@SpringBootApplication
@EnableConfigServer
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        System.setProperty("javax.net.debug", "ssl:handshake");

        logHostInfo();

        SpringApplication.run(Application.class, args);
    }

    private static void logHostInfo() {
        try {
            logger.info("Hostname:   {}", InetAddress.getLocalHost().getHostName());
            logger.info("IP address: {}", InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            logger.warn("Failed to read host info.", e);
        }
    }
}
