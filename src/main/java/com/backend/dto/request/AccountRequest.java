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
public class AccountRequest {

    private Long id;

    private Integer roleId;

    private String name;

    private String email;

    private String password;

    private String avatar;

    private Integer status;

    private String code;

    public AccountRequest(String name, String email, String password, String avatar) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
    }

}
