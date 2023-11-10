package com.backend.controller;

import com.backend.dto.request.OrderRequest;
import com.backend.dto.request.orderCustomer.OrderCutomerRequest;
import com.backend.dto.request.orderCustomer.SearchOrderCutomerRequest;
import com.backend.entity.Order;
import com.backend.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/order")
public class OrderControllerCustomer {
    @Autowired
    private IOrderService iOrderService;

    @PostMapping("/get-all/customer")
    public ResponseEntity<?> getAllOrder(@RequestBody SearchOrderCutomerRequest searchOrderCutomerRequest) {
        List<Order> listOrderCustomer = iOrderService.listAllByCustomer(searchOrderCutomerRequest);
        return ResponseEntity.ok().body(listOrderCustomer);
    }

    @PostMapping("/save")
    public ResponseEntity<?> add(@RequestBody OrderCutomerRequest orderCutomerRequest) {
        return ResponseEntity.ok().body(iOrderService.customerAddOrder(orderCutomerRequest));
    }
}
