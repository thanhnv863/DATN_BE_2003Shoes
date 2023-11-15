package com.backend.dto.response.orderDetail;

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
public class OrderDetailPDFResponse {

    private String codeShoeDetail;

    private String nameShoeDetail;

    private Integer quantity;

    private BigDecimal price;

    public BigDecimal getTotalAmount() {
        if (quantity != null && price != null) {
            return price.multiply(BigDecimal.valueOf(quantity));
        } else {
            return BigDecimal.ZERO;
        }
    }

}
