package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.ShoeRequest;
import com.backend.dto.response.ShoeResponse;
import com.backend.dto.response.shoedetail.DataPaginate;
import com.backend.dto.response.shoedetail.ResultItem;
import com.backend.entity.Shoe;

import java.util.List;

public interface IShoeService {

//    ServiceResult<?> resultValidate(String mess);
//
//    String validateNhanVien(ShoeRequest shoeRequest);
//
//    ServiceResult<Shoe> addNewShoe(ShoeRequest shoeRequest);

//    ServiceResult<List<ResultItem>> getAllShoeItems(int page, int size);

    ServiceResult<List<DataPaginate>> getAllShoeItemstest(int page, int pageSize, String nameShoe, Float sizeShoe, String brandShoe);

    ServiceResult<List<ShoeResponse>> getAllShoeName();

    ServiceResult<ShoeResponse> addNewShoeName(ShoeRequest shoeRequest);
}
