package com.backend.controller;

import com.backend.dto.request.shoe.ShoeRequestUpdate;
import com.backend.dto.request.size.SizeRequest;
import com.backend.dto.request.size.SizeRequestUpdate;
import com.backend.service.ISizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/size")
public class SizeController {
    @Autowired
    private ISizeService iSizeService;

    @GetMapping("/getAllSize")
    public ResponseEntity<?> getAllSize() {
        return ResponseEntity.ok(iSizeService.getAll());
    }

    @PostMapping("/addNewSize")
    public ResponseEntity<?> addNewSize(@RequestBody SizeRequest sizeRequest){
        return ResponseEntity.ok(iSizeService.addNewSize(sizeRequest));
    }

    @PostMapping("/updateSize")
    public ResponseEntity<?> updateSize(@RequestBody SizeRequestUpdate sizeRequestUpdate){
        return ResponseEntity.ok(iSizeService.updateSize(sizeRequestUpdate));
    }
}
