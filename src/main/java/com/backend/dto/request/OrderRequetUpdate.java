package com.backend.dto.request;

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
public class OrderRequetUpdate {

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

    @JsonSerialize(using = DateTimeSerializer.class)
    private Date desiredDate;

    @JsonSerialize(using = DateTimeSerializer.class)
    private Date receiveDate;

    private String updatedBy;

    private String note;

    private Integer status;

    private String specificAddress;

    private String ward;

    private Integer district;

    private Integer province;
}
