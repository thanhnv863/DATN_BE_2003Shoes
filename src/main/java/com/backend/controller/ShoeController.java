package com.backend.controller;

import com.backend.dto.request.ShoeRequest;
import com.backend.service.IShoeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/shoe")
public class ShoeController {

    @Autowired
    private IShoeService iShoeService;

//    @PostMapping("/addNewShoe")
////    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
//    public ResponseEntity<?> addNewShoe(@RequestBody ShoeRequest shoeRequest){
//        return ResponseEntity.ok(iShoeService.addNewShoe(shoeRequest));
//    }

    @GetMapping("/getAllShoeName")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getAllShoeName(){
        return ResponseEntity.ok(iShoeService.getAllShoeName());
    }

    @PostMapping("/addNewShoeName")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> addNewShoeName(@RequestBody ShoeRequest shoeRequest){
        return ResponseEntity.ok(iShoeService.addNewShoeName(shoeRequest));
    }
}
