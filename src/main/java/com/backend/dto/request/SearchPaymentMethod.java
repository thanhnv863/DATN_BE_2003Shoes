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
public class SearchPaymentMethod {

    private String orderCode;

    private String method;

    private BigDecimal priceMin;

    private BigDecimal priceMax;

    private Date dateFirst;

    private Date dateLast;

    private Integer status;

    private int page;

    private int size;

}
