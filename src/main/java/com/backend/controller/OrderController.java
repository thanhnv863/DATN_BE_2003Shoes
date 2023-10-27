package com.backend.controller;

import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.OrderRequest;
import com.backend.dto.request.OrderRequetUpdate;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.response.OrderReponse;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/order")
public class OrderController {

    @Autowired
    private IOrderService iOrderService;

    @PostMapping("/get-all")
    public ResponseEntity<?> getAllOrder(@RequestBody SearchOrderRequest searchOrderRequest) {
        Page<OrderReponse> page = iOrderService.searchOrder(searchOrderRequest);
        return ResponseEntity.ok().body(new ServiceResultReponse<>(AppConstant.SUCCESS, page.getTotalElements(), page.getContent(), "Lấy danh sách thành công "));
//        return ResponseEntity.ok().body(page);
    }

    @GetMapping("/get-one/{code}")
    public ResponseEntity<?> getOne(@PathVariable("code") String code) {
        return ResponseEntity.ok().body(iOrderService.getOne(code));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok().body(iOrderService.add(orderRequest));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updtate(@RequestBody OrderRequetUpdate orderRequetUpdate) {
        return ResponseEntity.ok().body(iOrderService.update(orderRequetUpdate));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody OrderRequetUpdate orderRequetUpdate) {
        return ResponseEntity.ok().body(iOrderService.delete(orderRequetUpdate));
    }

    @PostMapping("/get-order-by-status")
    public ResponseEntity<?> getOrderByStatus1() {
        Integer status = 1;
        return ResponseEntity.ok().body(iOrderService.getOrderByStatus(status));
    }
}
