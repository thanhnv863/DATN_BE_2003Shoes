package com.backend.controller;

import com.backend.dto.request.BrandRequest;
import com.backend.dto.request.CategoryRequest;
import com.backend.service.IBrandService;
import com.backend.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/brand")
public class BrandController {

    @Autowired
    private IBrandService iBrandService;

    @GetMapping("/getAllBrand")
    public ResponseEntity<?> getAllBrand() {
        return ResponseEntity.ok(iBrandService.getAll());
    }

    @PostMapping("/addNewBrand")
    public ResponseEntity<?> addNewBrand(@RequestBody BrandRequest brandRequest){
        return ResponseEntity.ok(iBrandService.addNewBrand(brandRequest));
    }

}
