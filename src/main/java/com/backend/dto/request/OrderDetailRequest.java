package com.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderDetailRequest {

    private Long id;

    private Long idShoeDetail;

    private Long idOrder;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal discount;

    private Integer status;
}
