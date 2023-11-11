package com.backend.dto.response;

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
public class AccountPageResponse {
    private List<Account> accounts;
    private List<Address> addresses;
    private int totalPages;
    private long totalElements;
    private int numberOfElements;
    private int size;
    private boolean last;
    private boolean first;
}
