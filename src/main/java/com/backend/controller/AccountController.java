package com.backend.controller;

import com.backend.dto.request.AccountRequest;
import com.backend.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired
    private IAccountService iAccountService;

    @PostMapping("/new-account")
    public ResponseEntity<?> addNewStaff(@RequestBody AccountRequest accountRequest){
        return ResponseEntity.ok(iAccountService.addAccount(accountRequest));
    }

    @GetMapping("/account")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(iAccountService.findAllAccount());
    }

    @PostMapping("/update-account")
    public ResponseEntity<?> updateStaff(@RequestBody AccountRequest accountRequest) throws IOException {
        return ResponseEntity.ok(iAccountService.updateAccount(accountRequest));
    }

    @PostMapping("/delete-account")
    public ResponseEntity<?> deleteStaff(@RequestBody AccountRequest accountRequest){
        return ResponseEntity.ok(iAccountService.huyAccount(accountRequest));
    }

    @GetMapping("/name-account")
    public ResponseEntity<?> searchNameStaff(@RequestParam("name") String name){
        return ResponseEntity.ok(iAccountService.searchNameAccount(name));
    }

    @GetMapping("/get-email-account")
    public ResponseEntity<?> getEmailStaff(String email){
       return ResponseEntity.ok(iAccountService.findByEmailAccount(email));
    }
}
