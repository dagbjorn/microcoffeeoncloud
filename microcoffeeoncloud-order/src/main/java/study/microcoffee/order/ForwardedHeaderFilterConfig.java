package study.microcoffee.order;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.ForwardedHeaderFilter;

/**
 * Servlet filter to make "Forwarded" and "X-Forwarded-*" headers available and make they reflect the client-originated protocol and
 * address in methods of the Servlet API.
 * <p>
 * This makes it possible to use ServletUriComponentsBuilder to build Location header URLs.
 */
@Configuration
public class ForwardedHeaderFilterConfig {

    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilterRegistrationBean() {
        FilterRegistrationBean<ForwardedHeaderFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(createForwardedHeaderFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // Second-highest
        return registrationBean;
    }

    private ForwardedHeaderFilter createForwardedHeaderFilter() {
        ForwardedHeaderFilter filter = new ForwardedHeaderFilter();
        return filter;
    }
}
