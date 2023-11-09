package com.backend.dto.request.orderCustomer;

import com.backend.util.DateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
public class OrderCutomerRequest {

    private Long id;

    private Long idVoucher;

    private Long idAccount;

    private String code;

    private String type;

    private String customerName;

    private String phoneNumber;

    private String address;

    private BigDecimal shipFee;

    private BigDecimal moneyReduce;

    private BigDecimal totalMoney;

    @JsonSerialize(using = DateTimeSerializer.class)
    private Date payDate;

    @JsonSerialize(using = DateTimeSerializer.class)
    private Date shipDate;


    private String note;

    private Integer status;
}
