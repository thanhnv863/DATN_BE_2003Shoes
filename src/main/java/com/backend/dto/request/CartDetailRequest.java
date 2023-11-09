package com.backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CartDetailRequest {
    private Long idCart;
    private Long idShoeDetail;
    private Integer qty;
    private Integer status;
}
