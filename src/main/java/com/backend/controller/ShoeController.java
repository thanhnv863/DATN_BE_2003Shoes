package com.backend.controller;

import com.backend.dto.request.color.ColorRequestUpdate;
import com.backend.dto.request.shoe.ShoeRequest;
import com.backend.dto.request.shoe.ShoeRequestUpdate;
import com.backend.service.IShoeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/getShoeByName/{name}")
    public ResponseEntity<?> getShoeByName(@PathVariable String name){
        return ResponseEntity.ok(iShoeService.getShoeByName(name));
    }

    @PostMapping("/updateShoe")
    public ResponseEntity<?> updateShoe(@RequestBody ShoeRequestUpdate shoeRequestUpdate){
        return ResponseEntity.ok(iShoeService.updateShoe(shoeRequestUpdate));
    }

    @PostMapping("/deleteShoe")
    public ResponseEntity<?> deleteShoe(@RequestBody ShoeRequestUpdate shoeRequestUpdate){
        return ResponseEntity.ok(iShoeService.deleteShoe(shoeRequestUpdate));
    }
}
