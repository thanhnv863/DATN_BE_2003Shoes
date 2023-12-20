package com.backend.service;

import com.backend.ServiceResult;
import com.backend.ServiceResultReponse;
import com.backend.dto.request.OrderRequetUpdate;
import com.backend.dto.response.OrderHistoryReponse;
import com.backend.entity.Order;
import com.backend.entity.OrderHistory;

import java.util.List;

public interface IOrderHistoryService {
    ServiceResult<?> listOrderHistoryByOrderCode(Long id);

    ServiceResult<?> addOrderHistory(OrderRequetUpdate orderRequetUpdate);
}
