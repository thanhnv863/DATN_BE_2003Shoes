package com.backend.service;

import com.backend.ServiceResultReponse;
import com.backend.dto.request.OrderDetailRequest;

public interface IOrderDetailSerivice {
    ServiceResultReponse<?> getAllOrderByOrderId(Integer pageNo, Long idOrder);

    ServiceResultReponse<?> addOrderDetail(OrderDetailRequest orderDetailRequest);

    ServiceResultReponse<?> updateOrderDetail(OrderDetailRequest orderDetailRequest);

    ServiceResultReponse<?> deleteOrderDetail(OrderDetailRequest orderDetailRequest);
}
