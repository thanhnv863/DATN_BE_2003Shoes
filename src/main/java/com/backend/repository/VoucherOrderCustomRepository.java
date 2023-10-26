package com.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface VoucherOrderCustomRepository {
    Page<Object> doSearch(Pageable pageable, String voucher,
                          Integer status);

    long getTotalCount(String voucher,
                       Integer status
    );

    List<Object> doSearchMinBillValue(BigDecimal totalMoneyMyOrder);
}
