package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.BrandRequest;
import com.backend.dto.request.SoleRequest;
import com.backend.dto.response.BrandResponse;
import com.backend.dto.response.SoleResponse;

import java.util.List;

public interface ISoleService {
    ServiceResult<List<SoleResponse>> getAll();

    ServiceResult<SoleResponse> addNewSole(SoleRequest soleRequest);
}
