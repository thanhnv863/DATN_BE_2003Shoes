package com.backend.controller;

import com.backend.dto.request.AddressRequest;
import com.backend.service.IAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {

    @Autowired
    private IAddressService iAddressService;

    public AddressController(IAddressService iAddressService) {
        this.iAddressService = iAddressService;
    }

    @PostMapping("/addnewaddress")
    public ResponseEntity<?> addNewAddress(@RequestBody AddressRequest addressRequest){
        return ResponseEntity.ok(iAddressService.addAddress(addressRequest));
    }

    @PostMapping("/updateaddress")
    public ResponseEntity<?> updateAddress(@RequestBody AddressRequest addressRequest){
        return ResponseEntity.ok(iAddressService.updateAddress(addressRequest));
    }

    @GetMapping("/address")
    public ResponseEntity<?> addressGetAll(){
        return ResponseEntity.ok(iAddressService.getAllAddress());
    }

    @PostMapping("/deleteaddress")
    public ResponseEntity<?> addressDelete(@RequestBody AddressRequest addressRequest){
        return ResponseEntity.ok(iAddressService.deleteAddress(addressRequest));
    }

    @GetMapping("/address-by-account-id/{id}")
    public ResponseEntity<?> addressByAccount(@PathVariable("id")Long id){
        return ResponseEntity.ok(iAddressService.getOneAddressByAccountId(id));
    }

    @PostMapping("/update-address-default")
    public ResponseEntity<?> updateAddressDefault(@RequestBody AddressRequest addressRequest){
        return ResponseEntity.ok(iAddressService.updateDefaultAddress(addressRequest));
    }

    @PostMapping("/getDefautAddressByAccountId")
    public ResponseEntity<?> getDefautAddressByAccountId(@RequestBody AddressRequest addressRequest){
        return ResponseEntity.ok(iAddressService.getDefaultAddressByAccountId(addressRequest));
    }

}
