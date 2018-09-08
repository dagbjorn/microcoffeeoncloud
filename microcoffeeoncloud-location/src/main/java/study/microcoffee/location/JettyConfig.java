package study.microcoffee.location;

import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.jetty.JettyServerCustomizer;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Additional Jetty configuration not supported by Spring Boot.
 */
@Configuration
@ConditionalOnProperty(value = "server.http.port", matchIfMissing = false)
public class JettyConfig {

    private final Logger logger = LoggerFactory.getLogger(JettyConfig.class);

    /**
     * Adds an additional network connector for use by a http port that comes in addition to the https port configured by the
     * standard Spring Boot properties in <code>application.properties</code>.
     *
     * @param httpPort
     *            the http port configured by the <code>server.http.port</code> property in <code>application.properties</code>.
     * @return The JettyServletWebServerFactory object.
     */
    @Bean
    public JettyServletWebServerFactory jettyServletWebServerFactory(@Value("${server.http.port}") final String httpPort) {
        logger.debug("Adding network connector of port {}", httpPort);

        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();

        factory.addServerCustomizers(new JettyServerCustomizer() {

            @Override
            public void customize(Server server) {
                NetworkTrafficServerConnector connector = new NetworkTrafficServerConnector(server);
                connector.setPort(Integer.valueOf(httpPort));
                server.addConnector(connector);
            }
        });

        return factory;
    }
}
