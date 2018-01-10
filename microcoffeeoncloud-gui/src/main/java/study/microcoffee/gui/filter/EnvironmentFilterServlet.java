package study.microcoffee.gui.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * Servlet for filtering resources containing references to environment-specific configuration variables on the format
 * <code>${CONFIG_PARAMETER}</code>.
 */
@WebServlet
public class EnvironmentFilterServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(EnvironmentFilterServlet.class);

    @Autowired
    private Environment environment;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // Autowire beans
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        logger.debug("Processing environment filter request for requestURI: {}", requestUri);

        // Prepend with 'static' used by Spring Boot as root folder for static resources.
        String resourcePath = "static" + requestUri;

        InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (resource == null) {
            throw new ServletException("Resource " + requestUri + " not found on server.");
        }

        ResourceFilter resourceFilter = new SpringEnvironmentResourceFilter(environment);

        StringWriter tempWriter = new StringWriter();
        resourceFilter.filterText(resource, tempWriter);

        String responseAsString = tempWriter.toString();

        logger.debug("Filtered configuration variables in response: {}", responseAsString);

        response.setContentLength(responseAsString.length());
        response.getWriter().print(responseAsString);
    }
}

@Configuration
class EnvironmentFilterServletConfig {

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        return new ServletRegistrationBean(new EnvironmentFilterServlet(), "/js/env.js");
    }
}
