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
public class DoanhThuTheoNgay {

    private BigDecimal tongTien;
    private BigDecimal soLuongHangHoa;
}
