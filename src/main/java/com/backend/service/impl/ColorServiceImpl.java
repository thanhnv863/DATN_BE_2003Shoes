package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.ColorRequest;
import com.backend.dto.response.CategoryResponse;
import com.backend.dto.response.ColorResponse;
import com.backend.entity.Category;
import com.backend.entity.Color;
import com.backend.repository.ColorRepository;
import com.backend.service.IColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ColorServiceImpl implements IColorService {

    @Autowired
    private ColorRepository colorRepository;

    @Override
    public ServiceResult<List<ColorResponse>> getAll() {
        List<Color> colorList = colorRepository.findAll();
        List<ColorResponse> colorResponses = convertToRes(colorList);
        return new ServiceResult<>(AppConstant.SUCCESS, "Category", colorResponses);
    }


    @Override
    public ServiceResult<ColorResponse> addNewColor(ColorRequest colorRequest) {
        Optional<Color> categoryOptional = colorRepository.findByNameColor(colorRequest.getName());
        if (categoryOptional.isPresent()) {
            return new ServiceResult(AppConstant.FAIL, "Category already exits!", null);
        } else {
            Color color = new Color();
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            color.setName(colorRequest.getName());
            color.setStatus(0);
            color.setCreatedAt(date);
            color.setUpdatedAt(date);
            return new ServiceResult(AppConstant.SUCCESS, "Category", colorRepository.save(color));
        }
    }


    private List<ColorResponse> convertToRes(List<Color> colorList) {
        return colorList.stream().map(color ->
                ColorResponse.builder()
                        .id(color.getId())
                        .name(color.getName())
                        .build()).collect(Collectors.toList());
    }
}

