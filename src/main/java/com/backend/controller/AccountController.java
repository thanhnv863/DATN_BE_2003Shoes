package com.backend.controller;

import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.AccountRequest;
import com.backend.dto.request.PasswordRequest;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.request.account.SearchAccountRequest;
import com.backend.dto.response.OrderReponse;
import com.backend.dto.response.account.AccountCustomResponse;
import com.backend.service.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<?> getAll(@RequestParam(name = "page",defaultValue = "0") int pageNo,@RequestParam(name = "size",defaultValue = "2") int pageSize){
        return ResponseEntity.ok(iAccountService.findAllAccount(pageNo,pageSize));
    }

    @PostMapping("/update-account")
    public ResponseEntity<?> updateStaff(@RequestBody AccountRequest accountRequest) throws IOException {
        return ResponseEntity.ok(iAccountService.updateAccount(accountRequest));
    }

    @PostMapping("/huy-account")
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

    @PostMapping("/change-password-account")
    public ResponseEntity<?> changePassword(@RequestBody PasswordRequest passwordRequest){
        return ResponseEntity.ok(iAccountService.changePassword(passwordRequest));
    }

    @PostMapping("/get-all")
    public ResponseEntity<?> getAllAccount(@RequestBody SearchAccountRequest searchAccountRequest) {
        Page<AccountCustomResponse> page = iAccountService.searchAccount(searchAccountRequest);
        return ResponseEntity.ok().body(new ServiceResultReponse<>(AppConstant.SUCCESS, page.getTotalElements(), page.getContent(), "Lấy danh sách thành công "));
//        return ResponseEntity.ok().body(page);
    }
}
