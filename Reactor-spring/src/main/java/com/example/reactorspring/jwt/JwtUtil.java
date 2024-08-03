package com.example.reactorspring.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {
    /*@Value("${app.jwt-secret-key}")
    private  final String SECRET_KEY;

    public String generateToken(String userName) {

        return Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public boolean validateJwtToken(String authToken) throws InsufficientAuthenticationException{
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
            throw new InsufficientAuthenticationException("Invalid JWT signature: "+e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            throw new InsufficientAuthenticationException("JWT token is expired: "+e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw new InsufficientAuthenticationException("JWT token is unsupported: "+e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new InsufficientAuthenticationException("JWT claims string is empty: "+e.getMessage());
        }catch (JwtException e){
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new InsufficientAuthenticationException("Invalid JWT token: "+e.getMessage());
        }
    }

    public Authentication getAuthentication(String accessToken) throws InsufficientAuthenticationException{
        Claims claims = parseClaims(accessToken);

        if(claims.get("adminYn") == null){
            throw new InsufficientAuthenticationException("Token without permission information");
        }

        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get("adminYn").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        org.springframework.security.core.userdetails.User user = User.of()
                .setId(claims.get("userId"))
                .setName(claims.get("username").toString());

        return new UsernamePasswordAuthenticationToken(user, "", authorities);
    }

    private Claims parseClaims(String accessToken){
        try {
            return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(accessToken).getBody();
        } catch(ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String extractUserName(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private boolean isTokenExpired(String token){
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());

    }*/
}
