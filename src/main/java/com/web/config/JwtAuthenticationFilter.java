package com.web.config;

import com.web.entity.User;
import com.web.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider tokens;
    private final UserRepository users;
    public JwtAuthenticationFilter(JwtTokenProvider tokens, UserRepository users) {
        this.tokens = tokens; this.users = users;
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                                FilterChain chain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            String jwt = authorization.substring(7);
            if (tokens.validateToken(jwt)) {
                users.findById(tokens.getUserIdFromJWT(jwt)).filter(u ->
                        Boolean.TRUE.equals(u.getActived()) && StringUtils.hasText(u.getRole()))
                        .ifPresent(u -> SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(u.getId().toString(), null,
                                        Collections.singleton(new SimpleGrantedAuthority(u.getRole())))));
            }
        }
        chain.doFilter(request, response);
    }
}
