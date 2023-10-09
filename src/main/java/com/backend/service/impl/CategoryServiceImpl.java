package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.CategoryRequest;
import com.backend.dto.response.CategoryResponse;
import com.backend.entity.Category;
import com.backend.repository.CategoryRepository;
import com.backend.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public ServiceResult<List<CategoryResponse>> getAll() {
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryResponse> categoryResponses = convertToRes(categoryList);
        return new ServiceResult<>(AppConstant.SUCCESS, "Category",categoryResponses);
    }

    @Override
    public ServiceResult<CategoryResponse> addNewCategory(CategoryRequest categoryRequest) {
        Optional<Category> categoryOptional = categoryRepository.findByNameCategory(categoryRequest.getName());
        if (categoryOptional.isPresent()){
            return new ServiceResult(AppConstant.FAIL,"Category already exits!",null);
        }else {
            Category category = new Category();
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            category.setName(categoryRequest.getName());
            category.setStatus(0);
            category.setCreatedAt(date);
            category.setUpdatedAt(date);
            return new ServiceResult(AppConstant.SUCCESS,"Category",categoryRepository.save(category));
        }

    }

    private List<CategoryResponse> convertToRes(List<Category> categoryList) {
        return categoryList.stream().map(category ->
                CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .build()).collect(Collectors.toList());
    }
}
