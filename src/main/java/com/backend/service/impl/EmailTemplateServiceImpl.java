package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.AddressRequest;
import com.backend.dto.request.EmailRequest;
import com.backend.dto.response.AddressResponse;
import com.backend.dto.response.EmailResponse;
import com.backend.entity.Account;
import com.backend.entity.Address;
import com.backend.entity.EmailTemplate;
import com.backend.repository.AccountRepository;
import com.backend.repository.EmailRepository;
import com.backend.service.IEmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmailTemplateServiceImpl implements IEmailTemplateService {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public ServiceResult<EmailResponse> addEmailTemplate(EmailRequest emailRequest) {
        EmailTemplate emailTemplate = new EmailTemplate();
        String result = validateEmail(emailRequest);

        if(result != null){
            return  result(result);
        }else{
            try{
                Optional<Account> accountId = accountRepository.findById(emailRequest.getAccountId());
                if(accountId.isPresent()){
                    Account account = accountId.get();
                    emailTemplate.setAccount(account);
                    emailTemplate.setMailType(emailRequest.getMailType());
                    emailTemplate.setMailContent(emailRequest.getMailContent());
                }else{
                    return new ServiceResult<>(AppConstant.FAIL,"Add fail",null);
                }

                emailTemplate = emailRepository.save(emailTemplate);
                EmailResponse convertEmailResponse = convertToResponse(emailTemplate);

                return new ServiceResult<>(AppConstant.SUCCESS, "Add thanh cong", convertEmailResponse);
            }catch (Exception e){
                e.printStackTrace();
                return new ServiceResult<>(AppConstant.BAD_REQUEST, e.getMessage(), null);
            }
        }

    }


    @Override
    public ServiceResult<EmailTemplate> updateEmailTemplate(EmailRequest emailRequest) {
        Optional<EmailTemplate> emailId = emailRepository.findById(emailRequest.getId());
        Optional<Account> accountId = accountRepository.findById(emailRequest.getAccountId());
        Account account = accountId.get();

        if (emailId.isPresent()){
            EmailTemplate emailExist = emailId.get();
            emailExist.setId(emailExist.getId());
            emailExist.setAccount(account);
            emailExist.setMailType(emailRequest.getMailType());
            emailExist.setMailContent(emailRequest.getMailContent());

            EmailTemplate emailTemplate = emailRepository.save(emailExist);

            return new ServiceResult<>(AppConstant.SUCCESS,"Success",emailTemplate);
        }

        return new ServiceResult<>(AppConstant.BAD_REQUEST,"fail",null);

    }

    @Override
    public ServiceResult<List<EmailResponse>> getAllEmail() {
        List<EmailTemplate> emailTemplateList = emailRepository.findAll();
        List<EmailResponse>emailResponses = new ArrayList<>();

        for (EmailTemplate emailTemplate: emailTemplateList){
            EmailResponse emailResponse = new EmailResponse();
            emailResponse.setId(emailTemplate.getId());
            emailResponse.setAccountId(emailTemplate.getAccount().getId());
            emailResponse.setMailType(emailTemplate.getMailType());
            emailResponse.setMailContent(emailTemplate.getMailContent());

            emailResponses.add(emailResponse);
        }

        return new ServiceResult<>(AppConstant.SUCCESS,
                "Successfully retrieved",
                emailResponses);
    }

    @Override
    public ServiceResult<EmailTemplate> deleteEmail(EmailRequest emailRequest) {
        Optional<EmailTemplate> optionalEmail = emailRepository.findById(emailRequest.getId());
        if (optionalEmail.isPresent()){
            EmailTemplate emailTemplate = optionalEmail.get();
            emailRepository.save(emailTemplate);
            return new ServiceResult<>(AppConstant.SUCCESS,"delete Success",null);
        }else{
            return new ServiceResult<>(AppConstant.FAIL,"Id not exist",null);
        }
    }

    @Override
    public void sendEmail(String to, String subject, String mailType,String mailContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        EmailTemplate emailTemplate = new EmailTemplate();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(mailType);
        message.setText(mailContent);
        emailSender.send(message);
    }

    @Override
    public ServiceResult<EmailResponse> saveEmail(EmailRequest emailRequest) {
        EmailTemplate emailTemplate = new EmailTemplate();

        emailTemplate.setMailType(emailRequest.getMailType());
        emailTemplate.setMailContent(emailRequest.getMailContent());


          EmailResponse emailResponseConvert = EmailResponse.builder()
                .mailType(emailTemplate.getMailType())
                .mailContent(emailTemplate.getMailContent())
                .build();

        emailTemplate = emailRepository.save(emailTemplate);
        EmailResponse convertEmailResponse = emailResponseConvert;

        return new ServiceResult<>(AppConstant.SUCCESS, "Add thanh cong", convertEmailResponse);
    }

    @Override
    public String validateEmail(EmailRequest emailRequest) {

        List<String> errorMessages = new ArrayList<>();

        if (emailRequest.getAccountId() == null){
            errorMessages.add("id_account không được để trông");
        }

        if (errorMessages.size() > 0) {
            return String.join(", ", errorMessages);
        } else {
            return null;
        }
    }


    @Override
    public EmailResponse convertToResponse(EmailTemplate emailTemplate){
        return EmailResponse.builder()
                .accountId(emailTemplate.getAccount().getId())
                .mailType(emailTemplate.getMailType())
                .mailContent(emailTemplate.getMailContent())
                .build();
    }

    @Override
    public ServiceResult<EmailResponse> result(String mess) {
        return new ServiceResult<>(AppConstant.FAIL,mess,null);
    }




}
