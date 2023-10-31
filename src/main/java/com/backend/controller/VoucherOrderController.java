package com.backend.controller;

import com.backend.ServiceResult;
import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.request.VoucherOrderRequest;
import com.backend.dto.response.OrderReponse;
import com.backend.dto.response.VoucherOrderResponse;
import com.backend.entity.VoucherOrder;
import com.backend.service.IVoucherOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/voucher-order")
public class VoucherOrderController {

    @Autowired
    private IVoucherOrderService voucherOrderService;

    @PostMapping("/get-all")
    public ResponseEntity<?> getAllVoucherOrder(@RequestBody VoucherOrderRequest voucherOrderRequest) {
        Page<VoucherOrderResponse> page = voucherOrderService.searchVoucher(voucherOrderRequest);
        return ResponseEntity.ok().body(new ServiceResultReponse<>(AppConstant.SUCCESS, page.getTotalElements(), page.getContent(), "Lấy danh sách thành công"));
    }

    @GetMapping("/get-one/{name}")
    public ResponseEntity<?> getOne(@PathVariable("name") String name) {
        return ResponseEntity.ok().body(voucherOrderService.getOne(name));
    }

    @PostMapping("/addNewVoucherOrder")
    public ResponseEntity<?> addVoucherOrder(@RequestBody VoucherOrderRequest voucherOrderRequest) {
        return ResponseEntity.ok(voucherOrderService.addVoucher(voucherOrderRequest));
    }

    @PostMapping("/updateVoucherOrder")
    public ResponseEntity<?> updateVoucherOrder(@RequestBody VoucherOrderRequest voucherOrderRequest) {
        return ResponseEntity.ok(voucherOrderService.updateVoucher(voucherOrderRequest));
    }

    @PostMapping("/deleteVoucherOrder")
    public ResponseEntity<?> deleteVoucherOrder(@RequestBody VoucherOrderRequest voucherOrderRequest) {
        return ResponseEntity.ok(voucherOrderService.deleteVoucher(voucherOrderRequest));
    }

    @PostMapping("/searchTotalMoneyMyOrder")
    public ResponseEntity<?> searchTotalMoneyMyOrder(@RequestBody VoucherOrderRequest voucherOrderRequest) {
        //return ResponseEntity.ok(voucherOrderService.searchTotalMoneyMyOrder(voucherOrderRequest));
        List<VoucherOrderResponse> list = voucherOrderService.searchTotalMoneyMyOrder(voucherOrderRequest);
        return ResponseEntity.ok().body(new ServiceResult<>(AppConstant.SUCCESS, "Lấy danh sách thành công",list));
    }

}
