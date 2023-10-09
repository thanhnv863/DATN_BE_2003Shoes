package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.CategoryRequest;
import com.backend.dto.request.ColorRequest;
import com.backend.dto.response.CategoryResponse;
import com.backend.dto.response.ColorResponse;

import java.util.List;

public interface IColorService {
    ServiceResult<List<ColorResponse>> getAll();

    ServiceResult<ColorResponse> addNewColor(ColorRequest colorRequest);

}
