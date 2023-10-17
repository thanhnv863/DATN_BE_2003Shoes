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

    //ServiceResult<List<DataPaginate>> getAllVoucherOrder(int page, int size);

    //ServiceResult<VoucherOrderResponse> deleteVoucher(Long id);

    ServiceResult<VoucherOrder> result(String mess);

    String validateVoucher(VoucherOrderRequest voucherOrderRequest);

//    ServiceResult<List<DataPaginate>> getAllVoucherOrderStatus0(int page, int size);
//
//    ServiceResult<List<DataPaginate>> getAllVoucherOrderStatus1(int page, int size);
//
//    ServiceResult<List<DataPaginate>> getAllVoucherOrderStatus2(int page, int size);
//
//    ServiceResult<List<DataPaginate>> searchAllVoucher(String name, int page, int size);

    Page<VoucherOrderResponse> searchVoucher(VoucherOrderRequest voucherOrderRequest);

    ServiceResultReponse<VoucherOrder> getOne(String code);
}
