package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.EmailRequest;
import com.backend.dto.request.RegisterRequest;
import com.backend.dto.response.RegisterResponse;
import com.backend.entity.Cart;
import com.backend.entity.Role;
import com.backend.entity.Account;
import com.backend.repository.AccountRepository;
import com.backend.repository.CartRepository;
import com.backend.service.IAccountService;
import com.backend.service.IEmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IEmailTemplateService iEmailTemplateService;

    @Override
    public ServiceResult<RegisterResponse> register(RegisterRequest registerRequest) {
        Account account = new Account();

        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        account.setName(registerRequest.getName());
        account.setEmail(registerRequest.getEmail());
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        account.setStatus(1);
        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        account.setRole(Role.builder().id(registerRequest.getIdRole()).build());
        account = accountRepository.save(account);
        Cart cart = new Cart();
        cart.setAccount(account);
        cart.setCreatedAt(now);
        cart.setUpdatedAt(now);
        cart.setStatus(1);
        cartRepository.save(cart);
        RegisterResponse registerResponse = new RegisterResponse();

        String to = registerRequest.getEmail();
        String subject = "Welcome to store 2003SHOES";
        String mailType = "chao mung nhan vien ";
        String mailContent = "Mật khẩu tài khoản của bạn là  :"+registerRequest.getPassword();

        iEmailTemplateService.sendEmail(to,subject,mailType,mailContent);
        return new ServiceResult<>(AppConstant.SUCCESS,
                "Registered Successfully",
                registerResponse.builder()
                        .email(account.getEmail())
                        .name(account.getName())
                        .build());
    }

    @Override
    public Boolean exitsEmail(RegisterRequest registerRequest) {
        Optional<Account> optionalAccount = accountRepository.findByEmail(registerRequest.getEmail());
        if (optionalAccount.isPresent()) {
            return true;
        } else {
            return false;
        }

    }




}
