package ru.kosinov.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        // This is not for websocket authorization, and this should most likely not be altered.
//        http
//                .httpBasic().disable()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//                .authorizeRequests().antMatchers("/ws-main").permitAll()
//                .anyRequest().denyAll();
        web.ignoring().antMatchers("/ws-main/**");
    }

}
