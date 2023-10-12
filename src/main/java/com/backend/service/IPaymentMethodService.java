package com.backend.service;

import com.backend.dto.request.SearchPaymentMethod;
import com.backend.dto.response.PaymentMethodReponse;
import org.springframework.data.domain.Page;

public interface IPaymentMethodService {
    Page<PaymentMethodReponse> searchOrder(SearchPaymentMethod searchPaymentMethod);
}
