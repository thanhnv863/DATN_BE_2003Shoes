package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.VoucherOrderRequest;
import com.backend.dto.response.VoucherOrderResponse;
import com.backend.dto.response.shoedetail.DataPaginate;
import com.backend.entity.VoucherOrder;
import org.springframework.data.domain.Page;

import javax.persistence.Tuple;
import java.util.List;

public interface IVoucherOrderService {
    ServiceResult<VoucherOrderResponse> addVoucher(VoucherOrderRequest voucherOrderRequest);

    ServiceResult<VoucherOrderResponse> updateVoucher(VoucherOrderRequest voucherOrderRequest,Long id);

    void updateVoucherStatus();

    VoucherOrderResponse convertToResponse(VoucherOrder voucherOrder);

    ServiceResult<List<DataPaginate>> getAllVoucherOrder(int page, int size);

    ServiceResult<VoucherOrderResponse> deleteVoucher(Long id);
}
