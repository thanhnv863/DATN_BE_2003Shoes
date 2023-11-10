package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.RegisterRequest;
import com.backend.dto.response.RegisterResponse;


public interface IAccountService {
    ServiceResult<RegisterResponse> register(RegisterRequest registerRequest);

    Boolean exitsEmail(RegisterRequest registerRequest);

}
