package study.microcoffee.creditrating;

import static org.springframework.security.config.Customizer.withDefaults;

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
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) {
        httpSecurity //
            .cors(withDefaults()) // Bypasses the authorization checks for OPTIONS requests
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/api/**").hasAuthority("SCOPE_creditrating");
                auth.anyRequest().permitAll();
            }) //
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));
        return httpSecurity.build();
    }
}
