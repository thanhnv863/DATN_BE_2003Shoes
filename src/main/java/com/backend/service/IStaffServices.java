package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.StaffRequest;
import com.backend.dto.response.StaffResponse;
import com.backend.entity.Account;

import java.io.IOException;
import java.util.List;

public interface IStaffServices {
    ServiceResult<StaffResponse> addStaff(StaffRequest staffRequest);

    ServiceResult<List<Account>> findAllStaff();

    ServiceResult<Account> updateStaff(StaffRequest staffRequest) throws IOException;

    ServiceResult<Account> deleteAddress(StaffRequest staffRequest);

    ServiceResult<List<Account>> searchNameStaff(String name);

    ServiceResult<Account> findByEmailStaff(String email);
}
