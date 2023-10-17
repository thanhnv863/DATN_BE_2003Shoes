package com.backend.dto.request;

import com.backend.util.DateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherOrderRequest {
    private Long id;

    private String code;

    private String name;

    private Integer quantity;

    private BigDecimal discountAmount;

    private BigDecimal minBillValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //@JsonSerialize(using = DateTimeSerializer.class)
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //@JsonSerialize(using = DateTimeSerializer.class)
    private LocalDateTime endDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //@JsonSerialize(using = DateTimeSerializer.class)
    private LocalDateTime createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //@JsonSerialize(using = DateTimeSerializer.class)
    private LocalDateTime updateAt;

    private Integer reduceForm;

    private Integer status;

    private int page;

    private int size;
}
