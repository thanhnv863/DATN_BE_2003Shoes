package com.backend.controller;

import com.backend.service.IOrderHistoryService;
import com.backend.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/order-history")
public class OrderHistoryController {
    @Autowired
    private IOrderHistoryService iOrderHistoryService;


    @GetMapping("/get-one/{idOrder}")
    public ResponseEntity<?> getOne(@PathVariable("idOrder") Long idOrder) {
        return ResponseEntity.ok().body(iOrderHistoryService.listOrderHistoryByOrderCode(idOrder));
    }

}
