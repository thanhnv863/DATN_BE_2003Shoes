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
public class HoaDonHuy {
    private BigDecimal hoaDonCho;
    private BigDecimal choThanhToan;
    private BigDecimal daThanhToan;
    private BigDecimal daHuy;
    private BigDecimal choXacNhan;
    private BigDecimal daXacNhan;
    private BigDecimal choGiaoHang;
    private BigDecimal daBanGiao;
    private BigDecimal hoanThanh;
}
