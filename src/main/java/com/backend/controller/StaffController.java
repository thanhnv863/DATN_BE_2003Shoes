package com.backend.controller;

import com.backend.dto.request.StaffRequest;
import com.backend.service.IEmailTemplateService;
import com.backend.service.IStaffServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/staff")
public class StaffController {

    @Autowired
    private IStaffServices iStaffServices;

    @PostMapping("/newstaff")
    public ResponseEntity<?> addNewStaff(@RequestBody StaffRequest staffRequest){
        return ResponseEntity.ok(iStaffServices.addStaff(staffRequest));
    }

    @GetMapping("/staff")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(iStaffServices.findAllStaff());
    }

    @PostMapping("/updatestaff")
    public ResponseEntity<?> updateStaff(@RequestBody StaffRequest staffRequest) throws IOException {
        return ResponseEntity.ok(iStaffServices.updateStaff(staffRequest));
    }

    @PostMapping("/deletestaff")
    public ResponseEntity<?> deleteStaff(@RequestBody StaffRequest staffRequest){
        return ResponseEntity.ok(iStaffServices.deleteAddress(staffRequest));
    }

    @GetMapping("/namestaff")
    public ResponseEntity<?> searchNameStaff(@RequestParam("name") String name){
        return ResponseEntity.ok(iStaffServices.searchNameStaff(name));
    }

    @GetMapping("/getemailstaff")
    public ResponseEntity<?> getEmailStaff(String email){
       return ResponseEntity.ok(iStaffServices.findByEmailStaff(email));
    }
}
