package com.backend.dto.statistical;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Top5SanPhamBanChayTrongThangVaNam {

    private String tenSanPham;
    private BigInteger soLuong;
}
