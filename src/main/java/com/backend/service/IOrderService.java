package com.backend.service;

import com.backend.ServiceResultReponse;
import com.backend.dto.request.OrderRequest;
import com.backend.dto.request.OrderRequetUpdate;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.response.OrderReponse;
import com.backend.entity.Order;
import org.springframework.data.domain.Page;

public interface IOrderService {

    Page<OrderReponse> searchOrder(SearchOrderRequest searchOrderRequest);

    ServiceResultReponse<Order> getOne(String code);

    ServiceResultReponse<Order> add(OrderRequest orderRequest);

    ServiceResultReponse<Order> update(OrderRequetUpdate orderRequetUpdate);

    ServiceResultReponse<Order> delete(OrderRequetUpdate orderRequetUpdate);

}
