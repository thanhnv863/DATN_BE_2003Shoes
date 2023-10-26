package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.response.ImagesResponse;
import com.backend.dto.response.ThumbnailResponse;
import com.backend.entity.Image;
import com.backend.entity.Thumbnail;
import com.backend.repository.ImageRepository;
import com.backend.service.IImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImagesServiceimpl implements IImagesService {

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public ServiceResult<List<ImagesResponse>> getAll() {
        List<Image> imageList = imageRepository.findAll();
        List<ImagesResponse> imagesResponseList = convertToImagesResponse(imageList);
        return new ServiceResult<>(AppConstant.SUCCESS,"get All Images succes",imagesResponseList);
    }

    private List<ImagesResponse> convertToImagesResponse(List<Image> imageList){
        return imageList.stream().map(image ->
                ImagesResponse.builder()
                        .id(image.getId())
                        .imgName(image.getImgName())
                        .imgUrl(image.getImgUrl())
                        .build()).collect(Collectors.toList());
    }
}
