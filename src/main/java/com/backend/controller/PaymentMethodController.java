package com.backend.controller;

import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.SearchPaymentMethod;
import com.backend.dto.request.paymentMethod.PaymentMethodRequest;
import com.backend.dto.request.paymentMethod.PaymentMethodRequestUpdate;
import com.backend.dto.response.PaymentMethodReponse;
import com.backend.service.IPaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/payment-method")
public class PaymentMethodController {

    @Autowired
    private IPaymentMethodService iPaymentMethodService;

    @PostMapping("/get-all")
    public ResponseEntity<?> getAllOrder(@RequestBody SearchPaymentMethod searchPaymentMethod) {
        Page<PaymentMethodReponse> page = iPaymentMethodService.searchPaymentMethod(searchPaymentMethod);
        return ResponseEntity.ok().body(new ServiceResultReponse<>(AppConstant.SUCCESS, page.getTotalElements(), page.getContent(), "Lấy danh sách thành công "));
//        return ResponseEntity.ok().body(page);
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody PaymentMethodRequest paymentMethodRequest) {
        return ResponseEntity.ok().body(iPaymentMethodService.add(paymentMethodRequest));
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody PaymentMethodRequestUpdate paymentMethodRequestUpdate) {
        return ResponseEntity.ok().body(iPaymentMethodService.update(paymentMethodRequestUpdate));
    }
}
