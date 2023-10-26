package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.request.CartDetailRequest;
import com.backend.dto.response.CartDetailResponse;
import com.backend.dto.response.CategoryResponse;
import com.backend.dto.response.shoedetail.ResultItem;
import com.backend.entity.Cart;
import com.backend.entity.CartDetail;
import com.backend.entity.ShoeDetail;
import com.backend.repository.CartDetailRepository;
import com.backend.repository.CartRepository;
import com.backend.repository.ShoeDetailCustomRepository;
import com.backend.repository.ShoeDetailRepository;
import com.backend.service.ICartDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CardDetailServiceImpl implements ICartDetailService {

    @Autowired
    private CartDetailRepository cartDetailRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ShoeDetailRepository shoeDetailRepository;

    @Autowired
    private ShoeDetailCustomRepository shoeDetailCustomRepository;

    @Override
    public ServiceResult<CartDetail> addCartDetailAtCart(CartDetailRequest cartDetailRequest) {
        Long cartId = cartDetailRequest.getIdCart();
        Long shoeDetailId = cartDetailRequest.getIdShoeDetail();
        Integer quantity = cartDetailRequest.getQty();

        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        Optional<ShoeDetail> optionalShoeDetail = shoeDetailRepository.findById(shoeDetailId);

        if (optionalCart.isPresent() && optionalShoeDetail.isPresent()) {
            Cart cart = optionalCart.get();
            ShoeDetail shoeDetail = optionalShoeDetail.get();

            CartDetail existingCartDetail = cartDetailRepository.findByCartAndShoeDetail(cart, shoeDetail);

            if (existingCartDetail != null) {
                existingCartDetail.setQuantity(quantity);
                cartDetailRepository.save(existingCartDetail);
                return new ServiceResult<>(AppConstant.SUCCESS, "Update cartdetail successfully!", null);
            } else {
                CartDetail newCartDetail = new CartDetail();
                newCartDetail.setCart(cart);
                newCartDetail.setShoeDetail(shoeDetail);
                newCartDetail.setQuantity(quantity);
                cartDetailRepository.save(newCartDetail);
                return new ServiceResult<>(AppConstant.SUCCESS, "Add new succesfully!", null);
            }
        } else {
            return new ServiceResult<>(AppConstant.NOT_FOUND, "Ko tim thay idcart, shoedetailis", null);
        }

    }

    @Override
    public ServiceResult<CartDetail> addCartDetailAtViewPageItem(CartDetailRequest cartDetailRequest) {
        Long cartId = cartDetailRequest.getIdCart();
        Long shoeDetailId = cartDetailRequest.getIdShoeDetail();
        Integer quantity = cartDetailRequest.getQty();

        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        Optional<ShoeDetail> optionalShoeDetail = shoeDetailRepository.findById(shoeDetailId);

        if (optionalCart.isPresent() && optionalShoeDetail.isPresent()) {
            Cart cart = optionalCart.get();
            ShoeDetail shoeDetail = optionalShoeDetail.get();

            CartDetail existingCartDetail = cartDetailRepository.findByCartAndShoeDetail(cart, shoeDetail);

            if (existingCartDetail != null) {
                existingCartDetail.setQuantity(existingCartDetail.getQuantity() + quantity);
                cartDetailRepository.save(existingCartDetail);
                return new ServiceResult<>(AppConstant.SUCCESS, "Update cartdetail successfully!", null);
            } else {
                CartDetail newCartDetail = new CartDetail();
                newCartDetail.setCart(cart);
                newCartDetail.setShoeDetail(shoeDetail);
                newCartDetail.setQuantity(quantity);
                cartDetailRepository.save(newCartDetail);
                return new ServiceResult<>(AppConstant.SUCCESS, "Add new succesfully!", null);
            }
        } else {
            return new ServiceResult<>(AppConstant.NOT_FOUND, "Ko tim thay idcart, shoedetailis", null);
        }
    }

    @Override
    public ServiceResult<CartDetail> deleteCartDetail(CartDetailRequest cartDetailRequest) {
        Long cartId = cartDetailRequest.getIdCart();
        Long shoeDetailId = cartDetailRequest.getIdShoeDetail();
        Integer quantity = cartDetailRequest.getQty();

        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        Optional<ShoeDetail> optionalShoeDetail = shoeDetailRepository.findById(shoeDetailId);

        if (optionalCart.isPresent() && optionalShoeDetail.isPresent()) {
            Cart cart = optionalCart.get();
            ShoeDetail shoeDetail = optionalShoeDetail.get();

            CartDetail existingCartDetail = cartDetailRepository.findByCartAndShoeDetail(cart, shoeDetail);

            if (existingCartDetail != null) {
                existingCartDetail.setQuantity(quantity);
                cartDetailRepository.delete(existingCartDetail);
                return new ServiceResult<>(AppConstant.SUCCESS, "Delete cartdetail successfully!", null);
            } else {
                return new ServiceResult<>(AppConstant.NOT_FOUND, "Record not found!", null);
            }
        } else {
            return new ServiceResult<>(AppConstant.NOT_FOUND, "Ko tim thay idcart, shoedetailis", null);
        }
    }

    @Override
    public ServiceResult<List<CartDetailResponse>> getCartDetailList(CartDetailRequest cartDetailRequest) {
        Long cartId = cartDetailRequest.getIdCart();
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            List<CartDetail> cartDetailList = cartDetailRepository.findByCart(cart);
            List<CartDetailResponse> cartDetailResponses = convertToCartDetailResponse(cartDetailList);
            return new ServiceResult<>(AppConstant.SUCCESS, "get list successfully!", cartDetailResponses);
        } else {
            return new ServiceResult<>(AppConstant.NOT_FOUND, "get list fail!", null);
        }
    }


    private List<CartDetailResponse> convertToCartDetailResponse(List<CartDetail> cartDetailList) {
        return cartDetailList.stream().map(CartDetail ->
                CartDetailResponse.builder()
                        .id(CartDetail.getShoeDetail().getId())
                        .quantity(CartDetail.getQuantity())
                        .detail((ResultItem) searchShoeDetailById(BigInteger.valueOf(CartDetail.getShoeDetail().getId())))
                        .build()).collect(Collectors.toList());


    }

    private Object searchShoeDetailById(BigInteger id) {
        Object result = shoeDetailCustomRepository.getOne(id);
        Object[] objects = (Object[]) result;
        ResultItem resultItem = convertToResultItem(objects);
        return resultItem;
    }

    private ResultItem convertToResultItem(Object[] objects) {
        ResultItem resultItem = new ResultItem();
        resultItem.setId(((BigInteger) objects[0]).longValue());
        resultItem.setNameShoe((String) objects[1]);
        resultItem.setSize((Float) objects[2]);
        resultItem.setCategory((String) objects[3]);
        resultItem.setBrand((String) objects[4]);
        resultItem.setSole((String) objects[5]);
        resultItem.setColor((String) objects[6]);
        resultItem.setCode((String) objects[7]);
        resultItem.setQrCode((String) objects[8]);
        resultItem.setPriceInput((BigDecimal) objects[9]);
        resultItem.setQty((Integer) objects[10]);
        resultItem.setCreatedAt((Date) objects[11]);
        resultItem.setUpdatedAt((Date) objects[12]);
        resultItem.setStatus((Integer) objects[13]);
        resultItem.setThumbnail((String) objects[14]);

        String inputString = (String) objects[15];
        List<String> listOfStrings = Arrays.stream(inputString.split(","))
                .collect(Collectors.toList());
        resultItem.setImages(listOfStrings);

        return resultItem;
    }

}