package com.backend.controller;

import com.backend.dto.request.orderCustomer.OrderCutomerRequest;
import com.backend.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer-no-login/order")
public class OrderCustomerNoLoginController {
    @Autowired
    private IOrderService iOrderService;

    @PostMapping("/save")
    public ResponseEntity<?> add(@RequestBody OrderCutomerRequest orderCutomerRequest) {
        return ResponseEntity.ok().body(iOrderService.customerNoLoginAddOrder(orderCutomerRequest));
    }
}
