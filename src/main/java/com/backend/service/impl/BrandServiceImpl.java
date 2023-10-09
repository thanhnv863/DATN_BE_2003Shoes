package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.BrandRequest;
import com.backend.dto.response.BrandResponse;
import com.backend.dto.response.CategoryResponse;
import com.backend.entity.Brand;
import com.backend.entity.Category;
import com.backend.repository.BrandRepository;
import com.backend.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BrandServiceImpl implements IBrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Override
    public ServiceResult<List<BrandResponse>> getAll() {
        List<Brand> brandList = brandRepository.findAll();
        List<BrandResponse> brandResponses = convertToRes(brandList);
        return new ServiceResult<>(AppConstant.SUCCESS, "brandList",brandResponses);
    }

    @Override
    public ServiceResult<BrandResponse> addNewBrand(BrandRequest brandRequest) {
        Optional<Brand> brandOptional = brandRepository.findByNameBrand(brandRequest.getName());
        if (brandOptional.isPresent()){
            return new ServiceResult(AppConstant.FAIL,"Category already exits!",null);
        }else {
            Brand brand = new Brand();
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            brand.setName(brandRequest.getName());
            brand.setStatus(0);
            brand.setCreatedAt(date);
            brand.setUpdatedAt(date);
            return new ServiceResult(AppConstant.SUCCESS,"Category",brandRepository.save(brand));
        }
    }

    private List<BrandResponse> convertToRes(List<Brand> brandList) {
        return brandList.stream().map(brand ->
                BrandResponse.builder()
                        .id(brand.getId())
                        .name(brand.getName())
                        .build()).collect(Collectors.toList());
    }
}
