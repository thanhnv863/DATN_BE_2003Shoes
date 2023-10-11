package com.backend.controller;

import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.OrderRequest;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.response.OrderReponse;
import com.backend.entity.Order;
import com.backend.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/order")

public class OrderController {

    @Autowired
    private IOrderService iOrderService;

    @PostMapping("/get-all")
    public ResponseEntity<?> getAllOrder(@RequestBody SearchOrderRequest searchOrderRequest) {
        Page<OrderReponse> page = iOrderService.searchOrder(searchOrderRequest);
//        return ResponseEntity.ok().body(new ServiceResultReponse<>(AppConstant.SUCCESS, page.getTotalElements(), page.getContent(), "Lấy danh sách thành công "));
        return ResponseEntity.ok().body(page);
    }

    @GetMapping("/get-one/{code}")
    public ResponseEntity<?> getOne(@PathVariable("code") String code) {
        Order order = iOrderService.getOne(code);
        return ResponseEntity.ok().body(new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, order, "Đã tìm thấy Order "));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok().body(iOrderService.add(orderRequest));
    }

}
