package study.microcoffee.creditrating;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration class for Spring Security.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity //
            .cors() // Bypasses the authorization checks for OPTIONS requests
            .and() //
            .authorizeRequests() //
            .mvcMatchers("/api/**").hasAuthority("SCOPE_creditrating") //
            .and() //
            .oauth2ResourceServer() //
            .jwt();
        return httpSecurity.build();
    }
}
