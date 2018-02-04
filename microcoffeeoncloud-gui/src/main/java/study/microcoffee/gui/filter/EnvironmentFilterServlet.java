package study.microcoffee.gui.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

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
        String resourceUri = getResourceUri(request);

        logger.debug("Processing environment filter request for resourceURI: {}", resourceUri);

        InputStream resource = getResourceAsStream(resourceUri);

        Map<String, String> envProps = resolveEnvironmentProperties();
        ResourceFilter resourceFilter = new MapResourceFilter(envProps);

        StringWriter tempWriter = new StringWriter();
        resourceFilter.filterText(resource, tempWriter);

        String responseAsString = tempWriter.toString();

        logger.debug("Filtered configuration variables in response: {}", responseAsString);

        response.setContentLength(responseAsString.length());
        response.getWriter().print(responseAsString);
    }

    /**
     * Returns the URI of the resource to filter. This is the request URI with the context root removed.
     */
    private String getResourceUri(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    /**
     * Returns the resource as an input stream. The resource is assumed to be located in Spring Boot's 'static' folder. This is the
     * root folder used by Spring Boot for static resources.
     */
    private InputStream getResourceAsStream(String resourceUri) throws ServletException {
        String resourcePath = "static" + resourceUri;

        InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (resource == null) {
            throw new ServletException("Resource " + resourcePath + " not found on server.");
        }
        return resource;
    }

    /**
     * Resolves environment-specific properties based on discovered services and returns the result in a map.
     */
    private Map<String, String> resolveEnvironmentProperties() {
        Map<String, String> envProps = new HashMap<>();

        String gatewayUrl = serviceResolver.resolveServiceUrl(gatewayServiceId);
        if (gatewayUrl != null) {
            envProps.put(GATEWAY_URL_PROP_NAME, gatewayUrl);
        } else {
            logger.error("No service discovered with service ID: {}", gatewayServiceId);
        }

        return envProps;
    }
}

@Configuration
class EnvironmentFilterServletConfig {

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        return new ServletRegistrationBean(new EnvironmentFilterServlet(), "/js/env.js");
    }
}
