package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.shoedetail.SearchShoeDetailRequest;
import com.backend.dto.request.shoedetail.ShoeDetailRequest;
import com.backend.dto.response.shoedetail.ResultItem;
import com.backend.entity.Shoe;
import org.springframework.data.domain.Page;

public interface IShoeDetailService {
    ServiceResult<Shoe> addNewShoe(ShoeDetailRequest shoeDetailRequest);

    Page<ResultItem> searchShoeDetail(SearchShoeDetailRequest searchShoeDetailRequest);

    ServiceResult<Shoe> resultValidate(String mess);
}
