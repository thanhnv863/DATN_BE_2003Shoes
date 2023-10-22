package com.backend.controller;

import com.backend.dto.request.CartDetailRequest;
import com.backend.service.ICartDetailService;
import com.backend.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @GetMapping("/find-cart/{id}")
    public ResponseEntity<?> findCartDetailByAccountId(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(iCartService.getCartByAccount(id));
    }
}
