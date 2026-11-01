package com.web.config;

import com.web.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider tokens;
    private final UserRepository users;
    public WebSecurityConfig(JwtTokenProvider tokens, UserRepository users) {
        this.tokens = tokens; this.users = users;
    }
    @Override protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().exceptionHandling()
            .authenticationEntryPoint((req,res,ex) -> res.sendError(HttpStatus.UNAUTHORIZED.value()))
            .and().authorizeRequests()
            .antMatchers("/api/login", "/api/regis", "/api/public/**").permitAll()
            .antMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
            .antMatchers("/api/system/**").hasAnyAuthority("ROLE_ADMIN","ROLE_LIBRARIAN")
            .antMatchers("/api/user/**").hasAnyAuthority("ROLE_USER","ROLE_ADMIN","ROLE_LIBRARIAN")
            .antMatchers("/api/all/**").hasAnyAuthority("ROLE_ADMIN","ROLE_USER")
            .antMatchers("/api/userlogged").authenticated()
            .anyRequest().denyAll()
            .and().apply(new JWTConfigurer(tokens, users));
    }
    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
