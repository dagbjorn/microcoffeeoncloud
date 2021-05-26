package study.microcoffee.order;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity //
            .csrf().disable() //
            .authorizeRequests() //
            .antMatchers("/**").permitAll() //
        // .and() //
        // .oauth2Client() // Only needed for the authorization code grant flow which we don't use.
        ;
    }
}
