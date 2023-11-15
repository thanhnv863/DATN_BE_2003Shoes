package com.backend.service;

import com.backend.dto.response.RefreshToken;
import com.backend.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class RefreshTokenService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtService jwtService;


    @Value("${jwt.refresh-token-expiration-time-seconds}")
    private int REFRESH_TOKEN_EXPIRATION_TIME_SECONDS;

    public RefreshToken createRefreshToken(String username,HttpServletResponse response) {
        String refreshToken = jwtService.generateRefreshToken(username);
        ZonedDateTime expiryDate = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")).plusSeconds(REFRESH_TOKEN_EXPIRATION_TIME_SECONDS);
        System.out.println("expiryDate" +expiryDate);
        setRefreshTokenCookie(refreshToken,response,expiryDate);

        return RefreshToken.builder()
                .token(refreshToken)
                .expiryDate(expiryDate.toInstant())
                .build();
    }

    private void setRefreshTokenCookie(String refreshToken,HttpServletResponse response,ZonedDateTime expiryDate) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        Instant instantNow = now.toInstant();
        long maxAgeSeconds = Duration.between(instantNow, expiryDate.toInstant()).getSeconds();
        cookie.setMaxAge(Math.toIntExact(maxAgeSeconds));
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}