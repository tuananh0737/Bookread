package com.web.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.web.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider tokenProvider;

    private final UserRepository userRepository;

    public WebSecurityConfig(JwtTokenProvider tokenProvider, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf()
                .disable()
                .exceptionHandling()
                .and()
                .authorizeRequests()
                .antMatchers("/api/public/**").permitAll()
                .antMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .antMatchers("/api/user/**").hasAuthority("ROLE_USER")
                .antMatchers("/api/all/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")
                .and()
                .apply(securityConfigurerAdapter()).and().sessionManagement()
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/login");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {

    }
    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider, userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

