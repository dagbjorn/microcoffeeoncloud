package study.microcoffee.creditrating;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Configuration class for Spring Security.
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests() //
            .mvcMatchers("/api/**").hasAuthority("SCOPE_creditrating") //
            .and() //
            .oauth2ResourceServer() //
            .jwt();
    }
}
