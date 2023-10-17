package com.backend.service;

import com.backend.ServiceResult;
import com.backend.ServiceResultReponse;
import com.backend.dto.request.VoucherOrderRequest;
import com.backend.dto.response.OrderReponse;
import com.backend.dto.response.VoucherOrderResponse;
import com.backend.dto.response.shoedetail.DataPaginate;
import com.backend.entity.Order;
import com.backend.entity.VoucherOrder;
import org.springframework.data.domain.Page;

import javax.persistence.Tuple;
import java.util.List;

public interface IVoucherOrderService {
    ServiceResult<VoucherOrder> addVoucher(VoucherOrderRequest voucherOrderRequest);

    ServiceResult<VoucherOrder> updateVoucher(VoucherOrderRequest voucherOrderRequest);

    void updateVoucherStatus();

    VoucherOrderResponse convertPage(Object[] object);

    ServiceResult<VoucherOrder> deleteVoucher(VoucherOrderRequest voucherOrderRequest);

    ServiceResult<VoucherOrder> result(String mess);

    String validateVoucher(VoucherOrderRequest voucherOrderRequest);

    Page<VoucherOrderResponse> searchVoucher(VoucherOrderRequest voucherOrderRequest);

    ServiceResultReponse<VoucherOrder> getOne(String code);
}
