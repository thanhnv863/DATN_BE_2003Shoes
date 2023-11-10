package com.backend.service;

import com.backend.ServiceResult;
import com.backend.ServiceResultReponse;
import com.backend.dto.request.SearchPaymentMethod;
import com.backend.dto.request.paymentMethod.PaymentMethodRequest;
import com.backend.dto.request.paymentMethod.PaymentMethodRequestUpdate;
import com.backend.dto.response.PaymentMethodReponse;
import com.backend.entity.Order;
import com.backend.entity.PaymentMethod;
import org.springframework.data.domain.Page;

public interface IPaymentMethodService {
    Page<PaymentMethodReponse> searchPaymentMethod(SearchPaymentMethod searchPaymentMethod);

    ServiceResultReponse<PaymentMethod> add(PaymentMethodRequest paymentMethodRequest);

    ServiceResultReponse<PaymentMethod> update(PaymentMethodRequestUpdate paymentMethodRequestUpdate);

    ServiceResult<?> getAllByIdOrder(Long idOrder);
}
