package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.response.ThumbnailResponse;
import com.backend.entity.Image;
import com.backend.entity.Thumbnail;

import java.util.List;

public interface IThumbnailService {
    ServiceResult<List<ThumbnailResponse>> getAll();
}
