package com.backend.controller;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.config.UserInfoUserDetailsService;
import com.backend.dto.Product;
import com.backend.dto.request.AuthRequest;
import com.backend.dto.request.RegisterRequest;
import com.backend.dto.response.JwtResponse;
import com.backend.dto.response.RefreshToken;
import com.backend.entity.Account;
import com.backend.repository.AccountRepository;
import com.backend.service.IAccountService;
import com.backend.service.JwtService;
import com.backend.service.RefreshTokenService;
import com.backend.service.impl.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private ProductService service;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserInfoUserDetailsService userDetailsService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private IAccountService iAccountService;

    @PostMapping("/signUp")
    public ResponseEntity<?> addNewUser(@RequestBody RegisterRequest registerRequest) {
        Boolean isExits = iAccountService.exitsEmail(registerRequest);
        if (isExits) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ServiceResult<>(AppConstant.BAD_REQUEST,"Email already exists"));
        }else {
            return ResponseEntity.ok(iAccountService.register(registerRequest));
        }

    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Product> getAllTheProducts() {
        return service.getProducts();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public Product getProductById(@PathVariable int id) {
        return service.getProduct(id);
    }


    @PostMapping("/login")
    @Transactional(rollbackFor = Exception.class)
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
            if (authentication.isAuthenticated()) {
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getEmail(), response);
                String accessToken = jwtService.generateToken(authRequest.getEmail());

                UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());

                Optional<Account> userInfoOptional = accountRepository.findByEmail(authRequest.getEmail());

                if (userInfoOptional.isPresent()) {
                    Account userInfo = new Account(userInfoOptional.get().getId(), userInfoOptional.get().getName(),userInfoOptional.get().getCode(), userDetails.getUsername(), userDetails.getPassword(),userInfoOptional.get().getAvatar(), userInfoOptional.get().getRole(), userDetails.getAuthorities());
                    return ResponseEntity.status(HttpStatus.OK).body(JwtResponse.builder()
                            .statusCode(AppConstant.SUCCESS)
                            .accessToken(accessToken)
                            .refreshToken(refreshToken.getToken())
                            .userInfo(userInfo)
                            .message("Đăng nhập thành công!")
                            .build());
                } else {
                    throw new UsernameNotFoundException("Không tìm thấy thông tin người dùng");
                }
            } else {
                throw new BadCredentialsException("Xác thực thất bại: Email hoặc mật khẩu không hợp lệ");
            }
        } catch (AuthenticationException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(JwtResponse.builder()
                    .statusCode(AppConstant.NOT_FOUND)
                    .message("Sai Email hoặc mật khẩu")
                    .build());
        }
    }

//    @PostMapping( "/refreshToken")
//    public JwtResponse refreshToken(HttpServletRequest request) {
//            String refreshTokenFromCookie = refreshTokenService.getRefreshTokenFromCookie(request);
//
//            if (refreshTokenFromCookie == null) {
//                return JwtResponse
//                        .builder()
//                        .statusCode(AppConstant.NOT_FOUND)
//                        .message("Refresh token is not in cookie!")
//                        .build();
//            }
//
//            if (jwtService.isRefreshTokenExpired(refreshTokenFromCookie)) {
////            throw new RuntimeException("Refresh token has expired. Please make a new signin request");
//                return JwtResponse
//                        .builder()
//                        .statusCode(AppConstant.NOT_FOUND)
//                        .message("Refresh token has expired. Please make a new signin request!")
//                        .build();
//            }
//
//            String email = jwtService.extractUsernameFromRefreshToken(refreshTokenFromCookie);
//            String accessToken = jwtService.generateToken(email);
//
//            return JwtResponse
//                    .builder()
//                    .accessToken(accessToken)
//                    .refreshToken(refreshTokenFromCookie)
//                    .message("New token has created!")
//                    .build();
//    }
@PostMapping("/refreshToken")
public JwtResponse refreshToken(HttpServletRequest request) {
    String refreshTokenFromCookie = refreshTokenService.getRefreshTokenFromCookie(request);

    if (refreshTokenFromCookie == null) {
        return JwtResponse
                .builder()
                .statusCode(HttpStatus.BAD_REQUEST.value()) // Change status code to BAD_REQUEST
                .message("Refresh token is not in cookie!")
                .build();
    }

    if (jwtService.isRefreshTokenExpired(refreshTokenFromCookie)) {
        return JwtResponse
                .builder()
                .statusCode(HttpStatus.BAD_REQUEST.value()) // Change status code to BAD_REQUEST
                .message("Refresh token has expired. Please make a new signin request!")
                .build();
    }

    String email = jwtService.extractUsernameFromRefreshToken(refreshTokenFromCookie);
    String accessToken = jwtService.generateToken(email);

    return JwtResponse
            .builder()
            .accessToken(accessToken)
            .refreshToken(refreshTokenFromCookie)
            .message("New token has created!")
            .build();
}


//    @GetMapping("/fetchAccount")
//    public ResponseEntity<Object> fetchAccountInfo(HttpServletRequest request) {
//        String authorizationHeader = request.getHeader("Authorization");
//        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
//            return new ResponseEntity<>("Access token is missing or invalid!", HttpStatus.UNAUTHORIZED);
//        }
//        String accessToken = authorizationHeader.substring(7);
//
//        String email = jwtService.extractEmail(accessToken);
//        System.out.println("email " + email);
//
//        Optional<Account> userInfoOptional = accountRepository.findByEmail(email);
//
//        if (userInfoOptional.isPresent()) {
//            Account userInfo = userInfoOptional.get();
//            return new ResponseEntity<>(userInfo, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("User not found for email: " + email, HttpStatus.NOT_FOUND);
//        }
//    }

    @GetMapping("/fetchAccount")
    public ResponseEntity<Object> fetchAccountInfo(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new ResponseEntity<>("Access token is missing or invalid!", HttpStatus.UNAUTHORIZED);
        }
        String accessToken = authorizationHeader.substring(7);

        if (accessToken == null || accessToken.isEmpty()) {
            return new ResponseEntity<>("Access token is missing or invalid!", HttpStatus.UNAUTHORIZED);
        }

        String email = jwtService.extractEmail(accessToken);
        System.out.println("email " + email);

        if (email == null || email.isEmpty()) {
            return new ResponseEntity<>("Access token is invalid!", HttpStatus.UNAUTHORIZED);
        }

        Optional<Account> userInfoOptional = accountRepository.findByEmail(email);

        if (userInfoOptional.isPresent()) {
            Account userInfo = userInfoOptional.get();
            return new ResponseEntity<>(userInfo, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found for email: " + email, HttpStatus.NOT_FOUND);
        }
    }


//    @PostMapping("/logout")
//    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response,HttpServletRequest request) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setDomain(request.getServerName());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.status(HttpStatus.OK).body(JwtResponse.builder()
                .statusCode(AppConstant.SUCCESS)
                .message("User logged out successfully")
                .build());
    }

//    @GetMapping("/forgotpassword")
//    public ResponseEntity<?> forgotPassword(@PathVariable("email") String email){
//        return ResponseEntity.ok(iAccountService.forgotPassword(email));
//    }

}
