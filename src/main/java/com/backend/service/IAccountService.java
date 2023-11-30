package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.AccountRequest;
import com.backend.dto.request.PasswordRequest;
import com.backend.dto.request.RegisterRequest;
import com.backend.dto.request.account.AccountAddress;
import com.backend.dto.request.account.SearchAccountRequest;
import com.backend.dto.response.AccountPageResponse;
import com.backend.dto.response.AccountResponse;
import com.backend.dto.response.RegisterResponse;
import com.backend.dto.response.account.AccountCustomResponse;
import com.backend.dto.response.account.AccountWithAddress;
import com.backend.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;


public interface IAccountService {
    ServiceResult<RegisterResponse> register(RegisterRequest registerRequest);

    Boolean exitsEmail(RegisterRequest registerRequest);

    ServiceResult<AccountResponse> addAccount(AccountRequest accountRequest);

    ServiceResult<AccountPageResponse> findAllAccount(int pageNo, int pageSize);

    ServiceResult<Account> updateAccount(AccountRequest accountRequest) throws IOException;

    ServiceResult<Account> huyAccount(AccountRequest accountRequest);

    ServiceResult<List<Account>> searchNameAccount(String name);

    ServiceResult<Account> findByEmailAccount(String email);

    ServiceResult<String> changePassword(PasswordRequest passwordRequest);

    Page<AccountCustomResponse> searchAccount(SearchAccountRequest searchAccountRequest);

    ServiceResult<Account> getOneAccount(Long id);

    ServiceResult<Account> kichHoatAccount(AccountRequest accountRequest);

    ServiceResult<List<AccountAddress>> getAllAddressAndAccount(Long id);
}
