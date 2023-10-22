package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.CartDetailRequest;
import com.backend.dto.response.CartDetailResponse;
import com.backend.entity.CartDetail;

import java.util.List;

public interface ICartDetailService {

    ServiceResult<CartDetail> addCartDetail(CartDetailRequest cartDetailRequest);


    ServiceResult<CartDetail> deleteCartDetail(CartDetailRequest cartDetailRequest);

    ServiceResult<List<CartDetailResponse>> getCartDetailList(CartDetailRequest cartDetailRequest);
}
