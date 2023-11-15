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
public class DataItemDoanhThu {

    private Integer year;
    private BigDecimal value;
    private BigDecimal count;
}
