package com.backend.dto.response.shoedetail;

import com.backend.dto.response.VoucherOrderResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataPaginate {
    private Meta meta;
    private List<ResultItem> result;
    private List<VoucherOrderResponse> voucherOrderResponses;
}
