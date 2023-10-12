package com.backend.service.impl;

import com.backend.dto.request.SearchPaymentMethod;
import com.backend.dto.response.PaymentMethodReponse;
import com.backend.repository.OrderRepository;
import com.backend.repository.PaymentMethodCustomRepository;
import com.backend.service.IPaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PaymentMethodServiceImpl implements IPaymentMethodService {
    @Autowired
    private PaymentMethodCustomRepository paymentMethodCustomRepository;

    @Autowired
    private OrderRepository orderRepository;

    public PaymentMethodReponse convertPage(Object[] object) {
        PaymentMethodReponse paymentMethodReponse = new PaymentMethodReponse();
        paymentMethodReponse.setId(((BigInteger) object[0]).longValue());
        paymentMethodReponse.setCode((String) object[1]);
        paymentMethodReponse.setMethod((String) object[2]);
        paymentMethodReponse.setTotal((BigDecimal) object[3]);
        paymentMethodReponse.setPaymentTime((Date) object[4]);
        paymentMethodReponse.setNote((String) object[5]);
        paymentMethodReponse.setStatus((Integer) object[6]);
        return paymentMethodReponse;
    }

    @Override
    public Page<PaymentMethodReponse> searchOrder(SearchPaymentMethod searchPaymentMethod) {
        Pageable pageable = PageRequest.of(searchPaymentMethod.getPage(), searchPaymentMethod.getSize());
        Page<Object> objects = paymentMethodCustomRepository.doSearch(
                pageable,
                searchPaymentMethod.getOrderCode(),
                searchPaymentMethod.getMethod(),
                searchPaymentMethod.getPriceMin(),
                searchPaymentMethod.getPriceMax(),
                searchPaymentMethod.getDateFirst(),
                searchPaymentMethod.getDateLast(),
                searchPaymentMethod.getStatus()

        );
        List<PaymentMethodReponse> list = new ArrayList<>();
        for (Object object : objects) {
            Object[] result = (Object[]) object;
            PaymentMethodReponse paymentMethodReponse = convertPage(result);
            list.add(paymentMethodReponse);
        }
        return new PageImpl<>(list, pageable, objects.getTotalElements());
    }
}
