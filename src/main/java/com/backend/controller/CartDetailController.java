package com.backend.controller;

import com.backend.dto.request.CartDetailRequest;
import com.backend.dto.request.brand.BrandRequest;
import com.backend.service.ICartDetailService;
import com.backend.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart-detail")
public class CartDetailController {

    @Autowired
    private ICartDetailService iCartDetailService;

    @PostMapping("/addCartDetail")
    public ResponseEntity<?> addCartDetail(@RequestBody CartDetailRequest cartDetailRequest){
        return ResponseEntity.ok(iCartDetailService.addCartDetail(cartDetailRequest));
    }

    @PostMapping("/deleteCartDetail")
    public ResponseEntity<?> deleteCartDetail(@RequestBody CartDetailRequest cartDetailRequest){
        return ResponseEntity.ok(iCartDetailService.deleteCartDetail(cartDetailRequest));
    }

    @PostMapping("/getListCartDetail")
    public ResponseEntity<?> getListCartDetail(@RequestBody CartDetailRequest cartDetailRequest){
        return ResponseEntity.ok(iCartDetailService.getCartDetailList(cartDetailRequest));
    }
}
