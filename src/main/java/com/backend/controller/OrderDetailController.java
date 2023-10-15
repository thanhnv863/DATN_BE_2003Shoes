package com.backend.controller;

import com.backend.dto.request.OrderDetailRequest;
import com.backend.service.IOrderDetailSerivice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/order-detail")
public class OrderDetailController {
    @Autowired
    private IOrderDetailSerivice iOrderDetailSerivice;

    @GetMapping("/list-order-detail/{idOrder}")
    public ResponseEntity<?> getOne(@RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo, @PathVariable("idOrder") Long idOrder) {
        return ResponseEntity.ok().body(iOrderDetailSerivice.getAllOrderByOrderId(pageNo, idOrder));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody OrderDetailRequest orderDetailRequest) {
        return ResponseEntity.ok().body(iOrderDetailSerivice.addOrderDetail(orderDetailRequest));
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody OrderDetailRequest orderDetailRequest) {
        return ResponseEntity.ok().body(iOrderDetailSerivice.updateOrderDetail(orderDetailRequest));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody OrderDetailRequest orderDetailRequest) {
        return ResponseEntity.ok().body(iOrderDetailSerivice.deleteOrderDetail(orderDetailRequest));
    }
}
