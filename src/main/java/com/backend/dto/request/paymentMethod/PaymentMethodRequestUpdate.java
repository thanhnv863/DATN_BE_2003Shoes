package com.backend.dto.request.paymentMethod;

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
public class PaymentMethodRequestUpdate {

    private Long id;

    private Long orderId;

    private String method;

    private BigDecimal total;

    private String note;

    private Integer status;
}
