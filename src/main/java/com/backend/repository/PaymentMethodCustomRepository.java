package com.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;

public interface PaymentMethodCustomRepository {
    Page<Object> doSearch(Pageable pageable,
                          String orderCode, String method,
                          BigDecimal priceMin, BigDecimal priceMax,
                          Date dateFirst, Date dateLast, Integer status);

    long getTotalCount(String orderCode, String method,
                       BigDecimal priceMin, BigDecimal priceMax,
                       Date dateFirst, Date dateLast, Integer status
    );
}
