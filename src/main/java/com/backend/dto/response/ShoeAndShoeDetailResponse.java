package com.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShoeAndShoeDetailResponse {
    private Long idShoe;

    private String nameShoe;

    private Long idBrand;

    private String nameBrand;

    private Long idCategory;

    private String nameCategory;

    private Long idSole;

    private String nameSole;

    private List<Long> idColor;

    private String nameColor;

    private List<Long> idSize;

    private String nameSize;

    private BigDecimal price;

    private Integer quantity;
}
