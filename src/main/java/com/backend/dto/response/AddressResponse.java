package com.backend.dto.response;

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
public class AddressResponse {
    private Long id;
    private Long accountId;
    private String name;
    private String phoneNumber;
    private String specificAddress;
    private Integer ward;
    private Integer district;
    private Integer province;
    private String note;
    private String defaultAddress;
}
