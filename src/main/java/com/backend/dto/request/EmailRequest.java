package com.backend.dto.request;

import com.backend.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EmailRequest {

    private Long id;
    private Long accountId;
    private String mailType;
    private String mailContent;

    public EmailRequest(String mailType, String mailContent) {
        this.mailType = mailType;
        this.mailContent = mailContent;
    }
}
