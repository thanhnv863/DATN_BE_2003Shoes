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
public class SoHoaDon {

    private BigDecimal tongTien;
    private BigInteger soHoaDon;
}
