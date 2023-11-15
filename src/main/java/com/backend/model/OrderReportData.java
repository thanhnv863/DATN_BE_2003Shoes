package com.backend.model;

import com.backend.dto.response.orderDetail.OrderDetailPDFResponse;
import com.backend.entity.Order;

import java.util.List;

public class OrderReportData {
    private Order order;
//    private List<OrderDetailPDFResponse> orderDetails;

    public OrderReportData() {
    }

//    public OrderReportData(Order order, List<OrderDetailPDFResponse> orderDetails) {
//        this.order = order;
//        this.orderDetails = orderDetails;
//    }

    public Order getOrder() {
        return order;
    }

//    public List<OrderDetailPDFResponse> getOrderDetails() {
//        return orderDetails;
//    }

    public void setOrder(Order order) {
        this.order = order;
    }

//    public void setOrderDetails(List<OrderDetailPDFResponse> orderDetails) {
//        this.orderDetails = orderDetails;
//    }
}

