package com.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public interface ShoeDetailCustomRepository {
    Page<Object> doSearch(Pageable pageable,String shoe,
                          List<Float> size, List<String> category, List<String> brand,
                          List<String> sole, List<String> color, BigDecimal minPrice,
                          BigDecimal maxPrice);

    long getTotalCount(String shoe, List<Float> size, List<String> category, List<String> brand,
                       List<String> sole, List<String> color, BigDecimal minPrice,
                       BigDecimal maxPrice
    );

    Object getOne(BigInteger id);
}
