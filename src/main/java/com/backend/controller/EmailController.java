package com.backend.controller;

import com.backend.dto.request.EmailRequest;
import com.backend.service.IEmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/emailtemplate")
public class EmailController {

    @Autowired
    private IEmailTemplateService iEmailTemplateService;

    @PostMapping("/savemail")
    public  ResponseEntity<?> saveEmail(@RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok(iEmailTemplateService.saveEmail(emailRequest));
    }

    @PostMapping("/emailnew")
    public ResponseEntity<?> addNewEmail(@RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok(iEmailTemplateService.addEmailTemplate(emailRequest));
    }

    @PostMapping("/updatemail")
    public ResponseEntity<?> updateMail(@RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok(iEmailTemplateService.updateEmailTemplate(emailRequest));
    }

    @GetMapping("/email")
    public ResponseEntity<?> emailGetAll(){
        return ResponseEntity.ok(iEmailTemplateService.getAllEmail());
    }

    @PostMapping("/deleteemail")
    public ResponseEntity<?> emailDelete(@RequestBody EmailRequest emailRequest){
        return ResponseEntity.ok(iEmailTemplateService.deleteEmail(emailRequest));
    }


}
