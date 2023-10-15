package com.backend.controller;

import com.backend.dto.request.AddressRequest;
import com.backend.dto.request.VoucherOrderRequest;
import com.backend.service.IAddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {

    private IAddressService iAddressService;

    public AddressController(IAddressService iAddressService) {
        this.iAddressService = iAddressService;
    }

    @PostMapping("/addnewaddress")
    public ResponseEntity<?> addNewAddress(@RequestBody AddressRequest addressRequest){
        return ResponseEntity.ok(iAddressService.addAddress(addressRequest));
    }

    @PutMapping("/updateaddress/{id}")
    public ResponseEntity<?> updateAddress(@RequestBody AddressRequest addressRequest,
                                           @PathVariable(name = "id") Long id){
        return ResponseEntity.ok(iAddressService.updateAddress(addressRequest,id));
    }

    @GetMapping("/address")
    public ResponseEntity<?> addressGetAll(){
        return ResponseEntity.ok(iAddressService.getAllAddress());
    }

    @DeleteMapping("/deleteaddress/{id}")
    public ResponseEntity<?> addressDelete(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(iAddressService.deleteAddress(id));
    }
}
