package com.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SearchOrderRequest {

    private String type;

    private String voucher;

    private String customer;

    private Date dateFirst;

    private Date dateLast;

    private Integer status;

    private BigDecimal priceMin;

    private BigDecimal priceMax;

    private int page;

    private int size;
}
