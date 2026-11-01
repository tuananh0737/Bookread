package com.web.config;

import com.web.service.CustomUserDetails;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final byte[] key;
    private final long expiration;
    public JwtTokenProvider(@Value("${JWT_SECRET_BASE64}") String secret,
                            @Value("${JWT_EXPIRATION_MS:604800000}") long expiration) {
        try { key = Base64.getDecoder().decode(secret); }
        catch (IllegalArgumentException ex) { throw new IllegalStateException("JWT secret must be base64", ex); }
        if (key.length < 64) throw new IllegalStateException("JWT secret must contain at least 64 random bytes");
        this.expiration = expiration;
    }
    public String generateToken(CustomUserDetails details) {
        Date now = new Date();
        return Jwts.builder().setSubject(details.getUser().getId().toString())
                .setIssuedAt(now).setExpiration(new Date(now.getTime() + expiration))
                .signWith(SignatureAlgorithm.HS512, key).compact();
    }
    public Long getUserIdFromJWT(String token) {
        return Long.valueOf(Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject());
    }
    public boolean validateToken(String token) {
        try { Jwts.parser().setSigningKey(key).parseClaimsJws(token); return true; }
        catch (JwtException | IllegalArgumentException ex) { return false; }
    }
}
