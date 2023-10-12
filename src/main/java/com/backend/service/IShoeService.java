package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.color.ColorRequestUpdate;
import com.backend.dto.request.shoe.ShoeRequest;
import com.backend.dto.request.shoe.ShoeRequestUpdate;
import com.backend.dto.response.ShoeResponse;
import com.backend.dto.response.shoedetail.DataPaginate;
import com.backend.entity.Color;
import com.backend.entity.Shoe;

import java.util.List;

public interface IShoeService {

    ServiceResult<Shoe> updateShoe(ShoeRequestUpdate shoeRequestUpdate);

    ServiceResult<List<DataPaginate>> getAllShoeItemstest(int page, int pageSize, String nameShoe, Float sizeShoe, String brandShoe);

    ServiceResult<List<ShoeResponse>> getAllShoeName();

    ServiceResult<ShoeResponse> addNewShoeName(ShoeRequest shoeRequest);
}
