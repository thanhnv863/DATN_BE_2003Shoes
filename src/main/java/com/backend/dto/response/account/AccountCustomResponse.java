package com.backend.dto.response.account;

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
public class AccountCustomResponse {

    private Long id;

    private String code;

    private String name;

    private String email;

    private String avatar;

    private Long roleId;

    private Integer status;

}
