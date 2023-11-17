package com.backend.controller;

import com.backend.dto.request.orderCustomer.OrderCutomerRequest;
import com.backend.dto.request.orderCustomer.SearchOrderCutomerRequest;
import com.backend.entity.Order;
import com.backend.service.IOrderDetailSerivice;
import com.backend.service.IOrderHistoryService;
import com.backend.service.IOrderService;
import com.backend.service.IPaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer/order")
public class OrderControllerCustomer {
    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private IOrderDetailSerivice iOrderDetailSerivice;

    @Autowired
    private IOrderHistoryService iOrderHistoryService;

    @Autowired
    private IPaymentMethodService iPaymentMethodService;

    @PostMapping("/get-all/customer")
    public ResponseEntity<?> getAllOrder(@RequestBody SearchOrderCutomerRequest searchOrderCutomerRequest) {
        List<Order> listOrderCustomer = iOrderService.listAllByCustomer(searchOrderCutomerRequest);
        return ResponseEntity.ok().body(listOrderCustomer);
    }

    @PostMapping("/save")
    public ResponseEntity<?> add(@RequestBody OrderCutomerRequest orderCutomerRequest) {
        return ResponseEntity.ok().body(iOrderService.customerAddOrder(orderCutomerRequest));
    }

    @PostMapping("/save-by-now")
    public ResponseEntity<?> addByNow(@RequestBody OrderCutomerRequest orderCutomerRequest) {
        return ResponseEntity.ok().body(iOrderService.customerByNow(orderCutomerRequest));
    }

    @GetMapping("/detail/{idOrder}")
    public ResponseEntity<?> getDetail(@RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo, @PathVariable("idOrder") Long idOrder) {
        return ResponseEntity.ok().body(iOrderDetailSerivice.getAllOrderByOrderId(pageNo, idOrder));
    }

    @GetMapping("/history/{idOrder}")
    public ResponseEntity<?> getHistory(@PathVariable("idOrder") Long idOrder) {
        return ResponseEntity.ok().body(iOrderHistoryService.listOrderHistoryByOrderCode(idOrder));
    }

    @GetMapping("/payment-method/{idOrder}")
    public ResponseEntity<?> getPaymentMethod(@PathVariable("idOrder") Long idOrder) {
        return ResponseEntity.ok().body(iPaymentMethodService.getAllByIdOrder(idOrder));
    }
}
