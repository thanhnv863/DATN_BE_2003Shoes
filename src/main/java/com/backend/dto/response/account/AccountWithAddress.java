package com.backend.dto.response.account;

import com.backend.entity.Account;
import com.backend.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AccountWithAddress {

    private Account account;
    private List<Address> address;
}
