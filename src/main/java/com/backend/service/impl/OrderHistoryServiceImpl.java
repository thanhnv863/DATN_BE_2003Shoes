package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.response.OrderHistoryReponse;
import com.backend.entity.OrderHistory;
import com.backend.repository.OrderHistoryRepository;
import com.backend.service.IOrderHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderHistoryServiceImpl implements IOrderHistoryService {

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Override
    public ServiceResult<?> listOrderHistoryByOrderCode(Long id) {
        List<OrderHistory> listOrderHistory = orderHistoryRepository.getListOrderHistoryByOrder(id);
        List<OrderHistoryReponse> listOrderHistoryReponse = listOrderHistory.stream()
                .map(this::convertOrderHistory)
                .collect(Collectors.toList());
        return new ServiceResult<>(AppConstant.SUCCESS, "Lấy danh sách thành công ", listOrderHistoryReponse);

    }

    public OrderHistoryReponse convertOrderHistory(OrderHistory orderHistory) {
        return OrderHistoryReponse.builder()
                .id(orderHistory.getId())
                .code(orderHistory.getOrder().getCode())
                .createdTime(orderHistory.getCreatedTime())
                .createdBy(orderHistory.getCreatedBy())
                .note(orderHistory.getNote())
                .build();
    }
}
