package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.response.ImagesResponse;
import com.backend.entity.Image;

import java.util.List;

public interface IImagesService {
    ServiceResult<List<ImagesResponse>> getAll();
}
