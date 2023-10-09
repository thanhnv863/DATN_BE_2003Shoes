package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.ShoeRequest;
import com.backend.dto.request.SoleRequest;
import com.backend.dto.response.BrandResponse;
import com.backend.dto.response.ShoeResponse;
import com.backend.dto.response.SoleResponse;
import com.backend.dto.response.shoedetail.DataPaginate;
import com.backend.entity.Brand;
import com.backend.entity.Sole;
import com.backend.repository.ShoeRepository;
import com.backend.repository.SoleRepository;
import com.backend.service.IShoeService;
import com.backend.service.ISoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SoleServiceImpl implements ISoleService {

    @Autowired
    private SoleRepository soleRepository;

    @Override
    public ServiceResult<List<SoleResponse>> getAll() {
        List<Sole> soleList = soleRepository.findAll();
        List<SoleResponse> soleResponses = convertToRes(soleList);
        return new ServiceResult<>(AppConstant.SUCCESS, "Get list Sole",soleResponses);
    }

    @Override
    public ServiceResult<SoleResponse> addNewSole(SoleRequest soleRequest) {
        Optional<Sole> soleOptional = soleRepository.findByNameSole(soleRequest.getName());
        if (soleOptional.isPresent()){
            return new ServiceResult(AppConstant.FAIL,"Sole already exits!",null);
        }else {
            Sole sole = new Sole();
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            sole.setName(soleRequest.getName());
            sole.setStatus(0);
            sole.setCreatedAt(date);
            sole.setUpdatedAt(date);
            return new ServiceResult(AppConstant.SUCCESS,"Category",soleRepository.save(sole));
        }
    }

    private List<SoleResponse> convertToRes(List<Sole> soleList) {
        return soleList.stream().map(sole ->
                SoleResponse.builder()
                        .id(sole.getId())
                        .name(sole.getName())
                        .build()).collect(Collectors.toList());
    }
}
