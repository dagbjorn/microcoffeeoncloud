package study.microcoffee.order;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity //
            .csrf(csrf -> csrf.disable()) // For now. (Something has changed in Spring Security 6.2.)
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
        return httpSecurity.build();
    }
}
