package com.backend.service;

import com.backend.ServiceResult;
import com.backend.ServiceResultReponse;
import com.backend.dto.request.OrderRequest;
import com.backend.dto.request.OrderRequetUpdate;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.response.OrderReponse;
import com.backend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;


public interface IOrderService {

    Page<OrderReponse> searchOrder(SearchOrderRequest searchOrderRequest);

    ServiceResultReponse<Order> getOne(String code);

    ServiceResultReponse<Order> add(OrderRequest orderRequest);

    ServiceResultReponse<Order> update(OrderRequetUpdate orderRequetUpdate);

    ServiceResultReponse<Order> delete(OrderRequetUpdate orderRequetUpdate);

    ServiceResultReponse<?> getOrderByStatus(Integer status);

}
