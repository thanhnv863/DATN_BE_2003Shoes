package com.backend.dto.response;

import com.backend.dto.response.shoedetail.ResultItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartDetailResponse {
    private Long id;
    private Integer quantity;
    private Integer status;
    private ResultItem detail;
}
