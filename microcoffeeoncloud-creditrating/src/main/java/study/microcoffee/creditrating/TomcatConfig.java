package study.microcoffee.creditrating;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Additional Tomcat configuration not supported by Spring Boot.
 */
@Configuration
@ConditionalOnProperty(value = "server.http.port", matchIfMissing = false)
public class TomcatConfig {

    private final Logger logger = LoggerFactory.getLogger(TomcatConfig.class);

    /**
     * Adds an additional network connector for use by a http port that comes in addition to the https port configured by the
     * standard Spring Boot properties in <code>application.properties</code>.
     *
     * @param httpPort
     *            the http port configured by the <code>server.http.port</code> property in <code>application.properties</code>.
     * @return The Tomcat customizer object.
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer(@Value("${server.http.port}") int httpPort) {
        logger.info("Adding network connector of port {}", httpPort);

        return (TomcatServletWebServerFactory factory) -> {
            final Connector connector = new Connector();
            connector.setScheme("http");
            connector.setPort(httpPort);
            factory.addAdditionalTomcatConnectors(connector);
        };
    }
}
