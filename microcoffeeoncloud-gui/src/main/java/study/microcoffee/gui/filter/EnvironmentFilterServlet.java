package study.microcoffee.gui.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import study.microcoffee.gui.common.discovery.DiscoveredServiceResolver;

/**
 * Servlet for filtering resources containing references to environment-specific configuration variables on the format
 * <code>${CONFIG_PARAMETER}</code>.
 */
@WebServlet
public class EnvironmentFilterServlet extends HttpServlet {

    private static final String GATEWAY_URL_PROP_NAME = "app.gateway.url";

    private final Logger logger = LoggerFactory.getLogger(EnvironmentFilterServlet.class);

    @Value("${app.gateway.serviceId}")
    private String gatewayServiceId;

    private ConcurrentMap<String, String> envProps = new ConcurrentHashMap<>();

    @Autowired
    private DiscoveredServiceResolver serviceResolver;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Autowire beans
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        resolveApiGateway();

        String requestUri = request.getRequestURI();

        logger.debug("Processing environment filter request for requestURI: {}", requestUri);

        // Prepend with 'static' used by Spring Boot as root folder for static resources.
        String resourcePath = "static" + requestUri;

        InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (resource == null) {
            throw new ServletException("Resource " + requestUri + " not found on server.");
        }

        ResourceFilter resourceFilter = new ConcurrentMapResourceFilter(envProps);

        StringWriter tempWriter = new StringWriter();
        resourceFilter.filterText(resource, tempWriter);

        String responseAsString = tempWriter.toString();

        logger.debug("Filtered configuration variables in response: {}", responseAsString);

        response.setContentLength(responseAsString.length());
        response.getWriter().print(responseAsString);
    }

    private void resolveApiGateway() {
        String gatewayUrl = serviceResolver.resolveServiceUrl(gatewayServiceId);
        envProps.put(GATEWAY_URL_PROP_NAME, gatewayUrl);
    }
}

@Configuration
class EnvironmentFilterServletConfig {

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        return new ServletRegistrationBean(new EnvironmentFilterServlet(), "/js/env.js");
    }
}
