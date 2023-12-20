package com.backend.service;

import com.backend.ServiceResultReponse;
import com.backend.dto.request.OrderRequest;
import com.backend.dto.request.OrderRequetUpdate;
import com.backend.dto.request.orderCustomer.OrderCutomerRequest;
import com.backend.dto.request.orderCustomer.SearchOrderCutomerRequest;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.response.OrderReponse;
import com.backend.entity.Order;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;


public interface IOrderService {

    Page<OrderReponse> searchOrder(SearchOrderRequest searchOrderRequest);

    ServiceResultReponse<Order> getOne(String code);

    ServiceResultReponse<Order> add(OrderRequest orderRequest);

    ServiceResultReponse<Order> update(OrderRequetUpdate orderRequetUpdate);

    ServiceResultReponse<Order> updateInformation(OrderRequetUpdate orderRequetUpdate);

    ServiceResultReponse<Order> updateTien(OrderRequetUpdate orderRequetUpdate);

    ServiceResultReponse<Order> delete(OrderRequetUpdate orderRequetUpdate);

    ServiceResultReponse<?> getOrderByStatus(Integer status);


    //customer
    List<Order> listAllByCustomer(SearchOrderCutomerRequest searchOrderCutomerRequest);

    ServiceResultReponse<Order> customerAddOrder(OrderCutomerRequest orderCutomerRequest);

    // customer by now
    ServiceResultReponse<Order> customerByNow(OrderCutomerRequest orderCutomerRequest);

    // customer no login
    ServiceResultReponse<Order> customerNoLoginAddOrder(OrderCutomerRequest orderCutomerRequest);

    // export
    List<OrderReponse> searchOrderExport(SearchOrderRequest searchOrderRequest);
    byte[] exportExcelListOrder(SearchOrderRequest searchOrderRequest) throws IOException;

}
