package com.backend.filter;

import com.backend.config.UserInfoUserDetails;
import com.backend.config.UserInfoUserDetailsService;
import com.backend.service.JwtService;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserInfoUserDetailsService userDetailsService;


//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String authHeader = request.getHeader("Authorization");
//        String token = null;
//        String username = null;
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            token = authHeader.substring(7);
//            username = jwtService.extractEmail(token);
//            System.out.println("token, username" + token + " " + username);
//        }
//
//        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//            UserInfoUserDetails userDetails = (UserInfoUserDetails) userDetailsService.loadUserByUsername(username);
//            System.out.println("userDetails" + userDetails);
//            System.out.println("Authorities: " + userDetails.getAuthorities());
//
//            if (jwtService.validateToken(token, userDetails)) {
//                System.out.println("Token hợp lệ cho người dùng " + userDetails.getUsername());
//
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                SecurityContextHolder.getContext().setAuthentication(authToken);
//            } else {
//                System.out.println("Token không hợp lệ");
//            }
//        }
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated()) {
//            System.out.println("Đã xác thực");
//        } else {
//            System.out.println("Chưa xác thực");
//        }
//
//        filterChain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                username = jwtService.extractEmail(token);
                System.out.println("token, username" + token + " " + username);
            }

            // ... (code removed for brevity)

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                System.out.println("Đã xác thực");
            } else {
                System.out.println("Chưa xác thực");
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // Token đã hết hạn
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token đã hết hạn");
        }
    }
}
