package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.brand.BrandRequestUpdate;
import com.backend.dto.request.category.CategoryRequest;
import com.backend.dto.request.category.CategoryRequestUpdate;
import com.backend.dto.response.CategoryResponse;
import com.backend.entity.Brand;
import com.backend.entity.Category;

import java.util.List;

public interface ICategoryService {
    ServiceResult<List<CategoryResponse>> getAll();

    ServiceResult<CategoryResponse> addNewCategory(CategoryRequest categoryRequest);

    ServiceResult<Category> updateCategory(CategoryRequestUpdate categoryRequestUpdate);
}
