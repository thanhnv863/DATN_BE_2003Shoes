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
    private String ward;
    private Integer district;
    private Integer province;
    private String note;
    private String defaultAddress;

    public AddressResponse(Long accountId, String name, String phoneNumber, String specificAddress,
                           String ward, Integer district, Integer province, String note, String defaultAddress) {
        this.accountId = accountId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.specificAddress = specificAddress;
        this.ward = ward;
        this.district = district;
        this.province = province;
        this.note = note;
        this.defaultAddress = defaultAddress;
    }
}
