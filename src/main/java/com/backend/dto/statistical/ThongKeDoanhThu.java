package com.backend.dto.statistical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThongKeDoanhThu {

    private Integer nam1;
    private BigDecimal tongTien1;
    private BigDecimal soLuong1;

    private Integer nam2;
    private BigDecimal tongTien2;
    private BigDecimal soLuong2;

    private Integer nam3;
    private BigDecimal tongTien3;
    private BigDecimal soLuong3;

    private Integer nam4;
    private BigDecimal tongTien4;
    private BigDecimal soLuong4;

    private Integer nam5;
    private BigDecimal tongTien5;
    private BigDecimal soLuong5;

}
