package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.category.CategoryRequestUpdate;
import com.backend.dto.request.color.ColorRequest;
import com.backend.dto.request.color.ColorRequestUpdate;
import com.backend.dto.response.ColorResponse;
import com.backend.entity.Category;
import com.backend.entity.Color;

import java.util.List;

public interface IColorService {
    ServiceResult<List<ColorResponse>> getAll();

    ServiceResult<ColorResponse> addNewColor(ColorRequest colorRequest);

    ServiceResult<Color> updateColor(ColorRequestUpdate colorRequestUpdate);

}
