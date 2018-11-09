package study.microcoffee.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import study.microcoffee.order.common.logging.HttpLoggingFilter;

/**
 * Configuration class of HTTP logging filter.
 * <p>
 * Configure the logging filter as follows:
 *
 * <pre>
 * logging.level.study.microcoffee.order.common.logging.HttpLoggingFilter = DEBUG
 * </pre>
 */
@Configuration
public class HttpLoggingFilterConfig {

    @Value("${formatted.logging:false}")
    private boolean formattedLogging;

    @Bean
    public FilterRegistrationBean<HttpLoggingFilter> requestLoggingFilterRegistrationBean() {
        FilterRegistrationBean<HttpLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(createRequestLogFilter());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    private HttpLoggingFilter createRequestLogFilter() {
        HttpLoggingFilter filter = new HttpLoggingFilter();
        filter.setFormattedLogging(formattedLogging);
        return filter;
    }
}
