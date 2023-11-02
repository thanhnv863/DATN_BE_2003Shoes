package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.ShoeDetailRequestUpdate;
import com.backend.dto.request.shoedetail.SearchShoeDetailRequest;
import com.backend.dto.request.shoedetail.ShoeDetailRequest;
import com.backend.dto.response.shoedetail.ResultItem;
import com.backend.entity.Shoe;
import org.springframework.data.domain.Page;

import java.math.BigInteger;
import java.util.List;

public interface IShoeDetailService {
    ServiceResult<Shoe> addNewShoe(ShoeDetailRequest shoeDetailRequest);

    Page<ResultItem> searchShoeDetail(SearchShoeDetailRequest searchShoeDetailRequest);

    ServiceResult<Shoe> resultValidate(String mess);

    Object searchById(BigInteger id);

    List<ResultItem> getShoeDetailsCustom(SearchShoeDetailRequest searchShoeDetailRequest);

    ServiceResult<Shoe> updateShoeDetail(ShoeDetailRequestUpdate shoeDetailRequestUpdate);

    ServiceResult<String> updateQtyShoeDetail(List<ShoeDetailRequestUpdate> shoeDetailRequestUpdateList);
}
