package com.backend.service;

import com.backend.ServiceResult;
import com.backend.entity.Cart;

public interface ICartService {
    ServiceResult<Cart> getCartByAccount (Long idAccount);
}
