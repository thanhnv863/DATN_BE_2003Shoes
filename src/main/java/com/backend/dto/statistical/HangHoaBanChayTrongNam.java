package com.backend.dto.statistical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HangHoaBanChayTrongNam {
    private String tenSanPham;
    private BigDecimal thang1;
    private BigDecimal thang2;
    private BigDecimal thang3;
    private BigDecimal thang4;
    private BigDecimal thang5;
    private BigDecimal thang6;
    private BigDecimal thang7;
    private BigDecimal thang8;
    private BigDecimal thang9;
    private BigDecimal thang10;
    private BigDecimal thang11;
    private BigDecimal thang12;
    private BigDecimal tongSoLuong;

}
