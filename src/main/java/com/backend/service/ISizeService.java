package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.BrandRequest;
import com.backend.dto.request.SizeRequest;
import com.backend.dto.response.BrandResponse;
import com.backend.dto.response.SizeResponse;

import java.util.List;

public interface ISizeService {
    ServiceResult<List<SizeResponse>> getAll();

    ServiceResult<SizeResponse> addNewSize(SizeRequest sizeRequest);

}
