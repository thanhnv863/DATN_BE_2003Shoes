package com.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VoucherOrderCustomRepository {
    Page<Object> doSearch(Pageable pageable, String voucher,
                          Integer status, LocalDateTime startDate, LocalDateTime endDate);

    long getTotalCount(String voucher,
                       Integer status, LocalDateTime startDate, LocalDateTime endDate
    );

    List<Object> doSearchMinBillValue(BigDecimal totalMoneyMyOrder);

    List<Object> searchExportListVoucher( String voucher, Integer status, LocalDateTime startDate, LocalDateTime endDate);
}
