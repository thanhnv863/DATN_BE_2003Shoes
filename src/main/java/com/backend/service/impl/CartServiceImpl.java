package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.entity.Account;
import com.backend.entity.Cart;
import com.backend.repository.AccountRepository;
import com.backend.repository.CartRepository;
import com.backend.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Access;
import java.util.Optional;

@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public ServiceResult<Cart> getCartByAccount(Long idAccount) {
        Optional<Account> optionalAccount =accountRepository.findById(idAccount);
        if (optionalAccount.isPresent()){
            Cart cart = cartRepository.findByAccount_Id(idAccount);
            return new ServiceResult<>(AppConstant.SUCCESS,"Cart retrieved",cart);
        }
        return new ServiceResult<>(AppConstant.NOT_FOUND,"Cart not found",null);
    }
}
