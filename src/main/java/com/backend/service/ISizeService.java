package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.size.SizeRequest;
import com.backend.dto.request.size.SizeRequestUpdate;
import com.backend.dto.request.sole.SoleRequestUpdate;
import com.backend.dto.response.SizeResponse;
import com.backend.entity.Size;
import com.backend.entity.Sole;

import java.util.List;

public interface ISizeService {
    ServiceResult<List<SizeResponse>> getAll();

    ServiceResult<SizeResponse> addNewSize(SizeRequest sizeRequest);

    ServiceResult<Size> updateSize(SizeRequestUpdate sizeRequestUpdate);

    ServiceResult<Size> deleteSize(SizeRequestUpdate sizeRequestUpdate);

    ServiceResult<Size> activeSize(SizeRequestUpdate sizeRequestUpdate);

}
