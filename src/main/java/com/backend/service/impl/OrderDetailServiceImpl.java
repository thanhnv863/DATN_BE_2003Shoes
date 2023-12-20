package com.backend.service.impl;

import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.OrderDetailRequest;
import com.backend.dto.response.OrderDetailReponse;
import com.backend.entity.Order;
import com.backend.entity.OrderDetail;
import com.backend.entity.OrderHistory;
import com.backend.entity.ShoeDetail;
import com.backend.repository.OrderDetailRepository;
import com.backend.repository.OrderHistoryRepository;
import com.backend.repository.OrderRepository;
import com.backend.repository.ShoeDetailRepository;
import com.backend.service.IOrderDetailSerivice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class OrderDetailServiceImpl implements IOrderDetailSerivice {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ShoeDetailRepository shoeDetailRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    public OrderDetailReponse convertToOrderDetail(OrderDetail orderDetail) {
        return OrderDetailReponse.builder()
                .id(orderDetail.getId())
                .codeShoeDetail(orderDetail.getShoeDetail().getCode())
                .codeOrder(orderDetail.getOrder().getCode())
                .quantity(orderDetail.getQuantity())
                .price(orderDetail.getPrice())
                .discount(orderDetail.getDiscount())
                .status(orderDetail.getStatus())
                .build();
    }

    @Override
    public ServiceResultReponse<?> getAllOrderByOrderId(Integer pageNo, Long idOrder) {
        Pageable pageable = PageRequest.of(pageNo, 10);
        Page<OrderDetailReponse> orderDetailResponses = orderDetailRepository.orderDetailByOrderId(pageable,idOrder);
        return new ServiceResultReponse<>(AppConstant.SUCCESS, orderDetailResponses.getTotalElements(), orderDetailResponses.getContent(), "Lấy danh sách thành công ");
    }

    @Override
    @Transactional
    public ServiceResultReponse<?> addOrderDetail(OrderDetailRequest orderDetailRequest) {
        Optional<ShoeDetail> shoeDetail = shoeDetailRepository.findById(orderDetailRequest.getIdShoeDetail());
        Optional<Order> order = orderRepository.findById(orderDetailRequest.getIdOrder());
        OrderDetail orderDetailCheck = orderDetailRepository.orderDetailByOrderAndShoeDetail(orderDetailRequest.getIdOrder(), orderDetailRequest.getIdShoeDetail());
        if (orderDetailCheck != null) {
            orderDetailCheck.setQuantity(orderDetailCheck.getQuantity() + orderDetailRequest.getQuantity());
            orderDetailCheck.setPrice(orderDetailRequest.getPrice());
            OrderDetail orderDetail1 = orderDetailRepository.save(orderDetailCheck);
            OrderDetailReponse orderDetailRequest1 = this.convertToOrderDetail(orderDetail1);
            return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderDetailRequest1, "Thành công!");
        } else {
            try {
                OrderDetail orderDetail = new OrderDetail();
                if (shoeDetail.isPresent() && order.isPresent()) {
                    ShoeDetail shoeDetailAddOrderDetail = shoeDetail.get();
                    Order orderAddOrderDetail = order.get();
                    orderDetail.setShoeDetail(shoeDetailAddOrderDetail);
                    orderDetail.setOrder(orderAddOrderDetail);
                    orderDetail.setQuantity(orderDetailRequest.getQuantity());
                    orderDetail.setPrice(orderDetailRequest.getPrice());
                    orderDetail.setDiscount(orderDetailRequest.getDiscount());
                    orderDetail.setStatus(0);
                    OrderDetail orderDetail1 = orderDetailRepository.save(orderDetail);
                    OrderDetailReponse orderDetailRequest1 = this.convertToOrderDetail(orderDetail1);
                    return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderDetailRequest1, "Thêm orderDetail thành công");
                } else {
                    return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Không tồn tại hóa đơn hoặc sản phẩm");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Thêm orderDetail thất bại");
            }
        }
    }

    @Override
    public ServiceResultReponse<?> updateOrderDetail(OrderDetailRequest orderDetailRequest) {
        Optional<OrderDetail> orderDetail = orderDetailRepository.findById(orderDetailRequest.getId());
        Optional<ShoeDetail> shoeDetail = shoeDetailRepository.findById(orderDetailRequest.getIdShoeDetail());
        Optional<Order> order = orderRepository.findById(orderDetailRequest.getIdOrder());
        if (orderDetail.isPresent() && shoeDetail.isPresent() && order.isPresent()) {
            if (orderDetailRequest.getQuantity() > shoeDetail.get().getQuantity()) {
                return new ServiceResultReponse<>(AppConstant.BAD_REQUEST, 0L, null, "Số lượng tồn không đủ!");
            }else {
                OrderDetail orderDetailUpdate = orderDetail.get();
                ShoeDetail shoeDetailUpdateOrderDetail = shoeDetail.get();
                Order orderUpdateOrderDetail = order.get();
                orderDetailUpdate.setShoeDetail(shoeDetailUpdateOrderDetail);
                orderDetailUpdate.setOrder(orderUpdateOrderDetail);
                orderDetailUpdate.setQuantity(orderDetailRequest.getQuantity());
                orderDetailUpdate.setPrice(orderDetailRequest.getPrice());
                orderDetailUpdate.setDiscount(orderDetailRequest.getDiscount());
                orderDetailUpdate.setStatus(orderDetailRequest.getStatus());
                OrderDetail orderDetail1 = orderDetailRepository.save(orderDetailUpdate);
                OrderDetailReponse orderDetailRequest1 = this.convertToOrderDetail(orderDetail1);
                return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderDetailRequest1, "Cập nhật orderDetail thành công");
            }

        } else {
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Không tồn tại chi tiết hóa đơn hoặc hóa đơn hoặc sản phẩm");
        }
    }

    @Override
    public ServiceResultReponse<?> deleteOrderDetail(OrderDetailRequest orderDetailRequest) {
        Optional<OrderDetail> orderDetail = orderDetailRepository.findById(orderDetailRequest.getId());
        if (orderDetail.isPresent()) {
            Date date = new Date();
            OrderDetail orderDetail1 = orderDetail.get();
            Order order = orderRepository.findById(orderDetailRequest.getIdOrder()).get();
            if(order.getStatus() == 4){
                OrderHistory orderHistory = new OrderHistory();
                    orderHistory.setOrder(order);
                    orderHistory.setCreatedTime(date);
                    orderHistory.setCreatedBy(order.getUpdatedBy());
                    orderHistory.setNote(order.getNote());
                    orderHistory.setType("Updated");
                    orderHistoryRepository.save(orderHistory);
            }
            orderDetailRepository.deleteById(orderDetail1.getId());

            return new ServiceResultReponse<>(AppConstant.SUCCESS, 0L, null, "Xóa orderDetail thành công");
        }
        return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Xóa orderDetail thất bại");
    }

}
