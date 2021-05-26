package study.microcoffee.order;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity //
            .csrf().disable() //
            .authorizeRequests() //
            .antMatchers(HttpMethod.GET, "/api/coffeeshop/menu").permitAll() //
            .antMatchers(HttpMethod.POST, "/api/coffeeshop/**/order").permitAll() //
            .antMatchers(HttpMethod.GET, "/api/coffeeshop/**/order/**").permitAll() //
            .antMatchers(HttpMethod.GET, "/apidocs/order/v2/api-docs").permitAll() //
            .antMatchers(HttpMethod.GET, "/internal/isalive").permitAll() //
            .antMatchers(HttpMethod.GET, "/internal/isready").permitAll() //
            .antMatchers(HttpMethod.GET, "/actuator/**").permitAll() //
            .anyRequest().authenticated() //
            //.and() //
            //.oauth2Client() // Only needed for the authorization code grant flow which we don't use.
            ;
    }
}
