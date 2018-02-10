package study.microcoffee.order;

import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.jetty.JettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Additional Jetty configuration not supported by Spring Boot.
 */
@Configuration
@ConditionalOnProperty(value = "server.http.port", matchIfMissing = false)
public class JettyConfig {

    /**
     * Adds an additional network connector for use by a http port that comes in addition to the https port configured by the
     * standard Spring Boot properties in <code>application.properties</code>.
     *
     * @param httpPort
     *            the http port configured by the <code>server.http.port</code> property in <code>application.properties</code>.
     * @return The JettyEmbeddedServletContainerFactory object.
     */
    @Bean
    public JettyEmbeddedServletContainerFactory jettyEmbeddedServletContainerFactory(
        @Value("${server.http.port}") final String httpPort) {

        JettyEmbeddedServletContainerFactory factory = new JettyEmbeddedServletContainerFactory();

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
