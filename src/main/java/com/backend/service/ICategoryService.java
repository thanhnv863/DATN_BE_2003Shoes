package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.CategoryRequest;
import com.backend.dto.response.CategoryResponse;
import com.backend.entity.Category;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ICategoryService {
    ServiceResult<List<CategoryResponse>> getAll();

    ServiceResult<CategoryResponse> addNewCategory(CategoryRequest categoryRequest);
}
