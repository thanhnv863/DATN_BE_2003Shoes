package com.backend.repository;

import com.backend.dto.response.OrderReponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface OrderCustomRepository {

    Page<Object> doSearch(Pageable pageable,
                                String type, String voucher, String customer,
                                Date dateFirst, Date dateLast, Integer status,
                                BigDecimal priceMin, BigDecimal priceMax);

    long getTotalCount(String type, String voucher, String customer,
                       Date dateFirst, Date dateLast, Integer status,
                       BigDecimal priceMin, BigDecimal priceMax
    );

    List<Object> getListExport(
                          String type, String voucher, String customer,
                          Date dateFirst, Date dateLast, Integer status,
                          BigDecimal priceMin, BigDecimal priceMax
    );
}
