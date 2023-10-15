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
    private List<Float> size;
    private List<String> category;
    private List<String> brand;
    private List<String> sole;
    private List<String> color;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private int page;
    private int pageSize;
}
