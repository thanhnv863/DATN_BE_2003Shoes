package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.OrderRequetUpdate;
import com.backend.dto.response.OrderHistoryReponse;
import com.backend.entity.Order;
import com.backend.entity.OrderHistory;
import com.backend.repository.OrderHistoryRepository;
import com.backend.repository.OrderRepository;
import com.backend.service.IOrderHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderHistoryServiceImpl implements IOrderHistoryService {

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public ServiceResult<?> listOrderHistoryByOrderCode(Long id) {
//        List<OrderHistory> listOrderHistory = orderHistoryRepository.getListOrderHistoryByOrder(id);
//        List<OrderHistoryReponse> listOrderHistoryReponse = listOrderHistory.stream()
//                .map(this::convertOrderHistory)
//                .collect(Collectors.toList());
        List<OrderHistoryReponse> listOrderHistoryReponse = orderHistoryRepository.getListOrderHistoryByOrder(id);
        return new ServiceResult<>(AppConstant.SUCCESS, "Lấy danh sách thành công ", listOrderHistoryReponse);

    }

    @Override
    public ServiceResult<?> addOrderHistory(OrderRequetUpdate orderRequetUpdate) {
        Optional<Order> order = orderRepository.findOrderByCode(orderRequetUpdate.getCode());
        Date date = new Date();
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrder(order.get());
        orderHistory.setCreatedTime(date);
        orderHistory.setCreatedBy(orderRequetUpdate.getUpdatedBy());
        orderHistory.setNote(orderRequetUpdate.getNote());
        orderHistory.setType("Updated");
        orderHistoryRepository.save(orderHistory);
        return new ServiceResult<>(AppConstant.SUCCESS, "Tạo lịch sử thành công ", orderHistory);
    }

    public OrderHistoryReponse convertOrderHistory(OrderHistory orderHistory) {
        return OrderHistoryReponse.builder()
//                .id(orderHistory.getId())
                .code(orderHistory.getOrder().getCode())
                .createdTime(orderHistory.getCreatedTime())
                .createdBy(orderHistory.getCreatedBy())
                .type(orderHistory.getType())
                .note(orderHistory.getNote())
                .build();
    }


}
