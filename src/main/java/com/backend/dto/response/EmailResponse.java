package com.backend.dto.response;

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
public class EmailResponse {
    private Long id;
    private String subject;
    private Integer mailType;
    private String mailContent;

    public EmailResponse(Integer mailType, String mailContent) {
        this.mailType = mailType;
        this.mailContent = mailContent;
    }
}
