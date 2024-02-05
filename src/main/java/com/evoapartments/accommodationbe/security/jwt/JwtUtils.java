package com.evoapartments.accommodationbe.security.jwt;

import com.evoapartments.accommodationbe.security.user.ApplicationUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${auth.token.jwtSecret}")
    private String JWT_SECRET;

    @Value("${auth.token.expirationMs}")
    private int JWT_EXPIRATION_TIME;

    public String generateJwtTokenForUser(Authentication authentication){
        ApplicationUserDetails userPrincipal = (ApplicationUserDetails) authentication.getPrincipal();
        List<String> userRoles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles", userRoles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + JWT_EXPIRATION_TIME))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
    }

    public String getUserNameFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
            return true;
        } catch (MalformedJwtException ex){
            logger.error("Jwt token is invalid : {}", ex.getMessage());
        } catch (ExpiredJwtException ex){
            logger.error("Jwt token has expired : {}", ex.getMessage());
        } catch (UnsupportedJwtException ex){
            logger.error("Jwt token is not supported : {}", ex.getMessage());
        } catch (IllegalArgumentException ex){
            logger.error("Claims not found : {}", ex.getMessage());
        }
        return false;
    }
}
