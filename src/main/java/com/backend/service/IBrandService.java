package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.BrandRequest;
import com.backend.dto.request.CategoryRequest;
import com.backend.dto.response.BrandResponse;
import com.backend.dto.response.CategoryResponse;

import java.util.List;

public interface IBrandService {

    ServiceResult<List<BrandResponse>> getAll();

    ServiceResult<BrandResponse> addNewBrand(BrandRequest brandRequest);
}
