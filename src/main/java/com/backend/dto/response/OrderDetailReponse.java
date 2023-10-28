package com.backend.dto.response;

import com.backend.entity.Order;
import com.backend.entity.ShoeDetail;
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
public class OrderDetailReponse {

    private Long id;

    private Long idShoeDetail;

    private String codeShoeDetail;

    private String imgUrl;

    private String codeOrder;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal discount;

    private Integer status;
}
