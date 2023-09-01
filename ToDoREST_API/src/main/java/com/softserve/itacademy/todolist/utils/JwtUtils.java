package com.softserve.itacademy.todolist.utils;

import com.softserve.itacademy.todolist.service.UserService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    private final Key jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    @Value("${myJwtToken.app.jwtExpirationsMs}")
    private int jwtExpirationsMs;
    private final UserService userService;

    public JwtUtils(UserService userService) {
        this.userService = userService;
    }

    public String generateTokenFromUsername(String username) {
        List<String> roleList = userService.loadUserByUsername(username).getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        String role = roleList.get(0);
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuer("myjwttoken")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationsMs))
                .signWith(jwtSecret)
                .compact();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: {}" + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: {}" + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: {}" + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: {}" + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("Signature validation failed" + e);
        }
        return false;
    }

    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    private Claims parseClaims(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token);

        return claimsJws.getBody();
    }
}

