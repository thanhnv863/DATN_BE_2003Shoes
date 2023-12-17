package com.backend.dto.request.shoedetail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SearchShoeDetailRequest {
    private String shoe;

    private List<Float> sizeList;
    private Float size;

    private List<String> categoryList;
    private String category;

    private List<String> brandList;
    private String brand;

    private List<String> soleList;
    private String sole;

    private List<String> colorList;
    private String color;

    private String sort;

    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private int page;
    private int pageSize;
    private Integer status;
}
