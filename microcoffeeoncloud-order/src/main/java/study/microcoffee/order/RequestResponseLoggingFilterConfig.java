package study.microcoffee.order;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import study.microcoffee.order.common.logging.JsonResponseLoggingFilter;

/**
 * Configuration class of request and response logging filters.
 * <p>
 * Configure the logging filters as follows:
 *
 * <pre>
 * logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG
 * logging.level.study.microcoffee.order.common.logging.JsonResponseLoggingFilter=DEBUG
 * </pre>
 */
@Configuration
public class RequestResponseLoggingFilterConfig {

    @Value("${formatted.logging:true}")
    private boolean formattedLogging;

    @Bean
    public FilterRegistrationBean<CommonsRequestLoggingFilter> requestLoggingFilterRegistrationBean() {
        FilterRegistrationBean<CommonsRequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(createRequestLogFilter());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }

    private CommonsRequestLoggingFilter createRequestLogFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludeHeaders(true);
        filter.setIncludeClientInfo(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setBeforeMessagePrefix("REQUEST DATA BEFORE : ");
        filter.setAfterMessagePrefix("REQUEST DATA AFTER : ");
        return filter;
    }

    @Bean
    public FilterRegistrationBean<JsonResponseLoggingFilter> responseLoggingFilterRegistrationBean() {
        FilterRegistrationBean<JsonResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(createResponseLogFilter());
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE - 1);
        return registrationBean;
    }

    private JsonResponseLoggingFilter createResponseLogFilter() {
        JsonResponseLoggingFilter filter = new JsonResponseLoggingFilter();
        filter.setFormattedLogging(formattedLogging);
        return filter;
    }
}
