package study.microcoffee.order;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import study.microcoffee.order.security.csrf.CsrfHeaderFilter;

/**
 * Configuration class for Spring Security.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity //
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll()) //
            .csrf(csrf -> csrf //
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Allow XSRF-TOKEN cookie to be read
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())) // Use Spring Security 5 default (non-BREACH)
            .addFilterAfter(new CsrfHeaderFilter(), BasicAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
