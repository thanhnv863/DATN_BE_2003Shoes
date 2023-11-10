package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.AccountRequest;
import com.backend.dto.request.RegisterRequest;
import com.backend.dto.response.AccountResponse;
import com.backend.dto.response.RegisterResponse;
import com.backend.entity.Account;

import java.io.IOException;
import java.util.List;


public interface IAccountService {
    ServiceResult<RegisterResponse> register(RegisterRequest registerRequest);

    Boolean exitsEmail(RegisterRequest registerRequest);

    ServiceResult<AccountResponse> addAccount(AccountRequest accountRequest);

    ServiceResult<List<Account>> findAllAccount();

    ServiceResult<Account> updateAccount(AccountRequest accountRequest) throws IOException;

    ServiceResult<Account> huyAccount(AccountRequest accountRequest);

    ServiceResult<List<Account>> searchNameAccount(String name);

    ServiceResult<Account> findByEmailAccount(String email);
}
