package com.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.refresh-token-expiration-time-seconds}")
    private int REFRESH_TOKEN_EXPIRATION_TIME_SECONDS;

    public static final String SECRET = "SECRETKEYACCESSTOKENSECRETKEYACCESSTOKENSECRETKEYACCESSTOKEN";


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        System.out.println("Claims: " + claims);
        return extractClaim(token, claims1 -> claims.get("sub", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        String username = extractEmail(token);
        System.out.println("username"+username);
        System.out.println("userDetails.getUsername()"+ userDetails.getUsername());
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }


    public String generateToken(String userName){
        Map<String,Object> claims=new HashMap<>();
        return createAccessToken(claims,userName);
    }

    private String createAccessToken(Map<String, Object> claims, String userName) {
        long currentTimeMillis = System.currentTimeMillis();
        long expirationMillis = currentTimeMillis + (18000 * 10000);
//        long expirationMillis = currentTimeMillis + (10 * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(expirationMillis))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

//refresh token


//    private static final String SECRET_KEY = "SECRETKEYREFRESHTOKENSECRETKEYREFRESHTOKENSECRETKEYREFRESHTOKEN";
//    public static final int REFRESH_TOKEN_EXPIRATION_TIME_SECONDS = 86400;



    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("exp", Date.from(Instant.now().plusSeconds(REFRESH_TOKEN_EXPIRATION_TIME_SECONDS)));

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    private Claims extractClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsernameFromRefreshToken(String token) {
        return extractClaimsFromToken(token).getSubject();
    }

    public Date extractExpirationDateFromRefreshToken(String token) {
        return extractClaimsFromToken(token).getExpiration();
    }

    public boolean isRefreshTokenExpired(String token) {
        Date expiration = extractExpirationDateFromRefreshToken(token);
        return expiration.before(new Date());
    }

}
