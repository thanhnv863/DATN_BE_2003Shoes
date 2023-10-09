package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.SizeRequest;
import com.backend.dto.response.SizeResponse;
import com.backend.dto.response.SoleResponse;
import com.backend.entity.Size;
import com.backend.entity.Sole;
import com.backend.repository.SizeRepository;
import com.backend.service.ISizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SizeServiceImpl implements ISizeService {

    @Autowired
    private SizeRepository sizeRepository;

    @Override
    public ServiceResult<List<SizeResponse>> getAll() {
        List<Size> sizeList = sizeRepository.findAll();
        List<SizeResponse> sizeResponse = convertToRes(sizeList);
        return new ServiceResult<>(AppConstant.SUCCESS, "Get list Size",sizeResponse);
    }

    @Override
    public ServiceResult<SizeResponse> addNewSize(SizeRequest sizeRequest) {
        Optional<Size> sizeOptional = sizeRepository.findByNameSize(sizeRequest.getName());
        if (sizeOptional.isPresent()){
            return new ServiceResult(AppConstant.FAIL,"Size already exits!",null);
        }else {
            Size size = new Size();
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            size.setName(sizeRequest.getName());
            size.setStatus(1);
            size.setCreatedAt(date);
            size.setUpdatedAt(date);
            return new ServiceResult(AppConstant.SUCCESS,"Category",sizeRepository.save(size));
        }
    }

    private List<SizeResponse> convertToRes(List<Size> sizeList) {
        return sizeList.stream().map(size ->
                SizeResponse.builder()
                        .id(size.getId())
                        .name(size.getName())
                        .build()).collect(Collectors.toList());
    }
}
