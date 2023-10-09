package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.ShoeDetailRequest;
import com.backend.dto.request.ShoeRequest;
import com.backend.entity.Shoe;

public interface IShoeDetailService {
    ServiceResult<Shoe> addNewShoe(ShoeDetailRequest shoeDetailRequest);

    String validateNhanVien(ShoeDetailRequest shoeDetailRequest);

    ServiceResult<Shoe> resultValidate(String mess);
}
