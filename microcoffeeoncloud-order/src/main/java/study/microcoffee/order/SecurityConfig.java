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
            .csrf().disable() //
            .authorizeHttpRequests(auth -> auth.antMatchers("/**").permitAll()) //
        // .and() //
        // .oauth2Client() // Only needed for the authorization code grant flow which we don't use.
        ;
        return httpSecurity.build();
    }
}
