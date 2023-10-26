package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.EmailRequest;
import com.backend.dto.response.EmailResponse;
import com.backend.entity.EmailTemplate;

import java.util.List;

public interface IEmailTemplateService {

    ServiceResult<EmailResponse> addEmailTemplate(EmailRequest emailRequest);

    ServiceResult<EmailTemplate> updateEmailTemplate(EmailRequest emailRequest);

    ServiceResult<List<EmailResponse>> getAllEmail();

    ServiceResult<EmailTemplate> deleteEmail(EmailRequest emailRequest);

    String validateEmail(EmailRequest emailRequest);

    EmailResponse convertToResponse(EmailTemplate emailTemplate);

    ServiceResult<EmailResponse> result(String mess);

    void sendEmail(String to, String subject, String mailType, String mailContent);

    ServiceResult<EmailResponse> saveEmail(EmailRequest emailRequest);


}
