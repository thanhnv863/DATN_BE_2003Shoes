package com.backend.dto.request;

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
public class AddressRequest {

    private Long id;

    private Long accountId;

    private String name;

    private String phoneNumber;

    private String specificAddress;

    private String ward;

    private Integer district;

    private Integer province;

    private String note;

    private String defaultAddress;

    public AddressRequest(Long accountId) {
        this.accountId = accountId;
    }
}
