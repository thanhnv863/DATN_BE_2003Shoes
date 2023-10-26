package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.response.ThumbnailResponse;
import com.backend.entity.Thumbnail;
import com.backend.repository.ThumbnailRepository;
import com.backend.service.IThumbnailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThumbnailServiceImpl implements IThumbnailService {

    @Autowired
    private ThumbnailRepository thumbnailRepository;

    @Override
    public ServiceResult<List<ThumbnailResponse>> getAll() {
        List<Thumbnail> thumbnailList = thumbnailRepository.findAll();
        List<ThumbnailResponse> thumbnailResponseList = convertToThumbnailResponse(thumbnailList);
        return new ServiceResult<>(AppConstant.SUCCESS,"get All Thumbnail succes",thumbnailResponseList);
    }

    private List<ThumbnailResponse> convertToThumbnailResponse(List<Thumbnail> thumbnailList){
        return thumbnailList.stream().map(thumbnail ->
                ThumbnailResponse.builder()
                        .id(thumbnail.getId())
                        .imgName(thumbnail.getImgName())
                        .imgUrl(thumbnail.getImgUrl())
                        .build()).collect(Collectors.toList());
    }
}
