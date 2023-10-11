package com.backend.service.impl;

import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.OrderRequest;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.response.OrderReponse;
import com.backend.entity.Order;
import com.backend.repository.OrderCustomRepository;
import com.backend.repository.OrderRepository;
import com.backend.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderCustomRepository orderCustomRepository;

    @Autowired
    private OrderRepository orderRepository;

    public OrderReponse convertPage(Object[] object) {
        OrderReponse orderReponse = new OrderReponse();
        orderReponse.setId(((BigInteger) object[0]).longValue());
        orderReponse.setNameVoucher((String) object[1]);
        orderReponse.setCode((String) object[2]);
        orderReponse.setType((String) object[3]);
        orderReponse.setCustomerName((String) object[4]);
        orderReponse.setPhoneNumber((String) object[5]);
        orderReponse.setAddress((String) object[6]);
        orderReponse.setShipFee((BigDecimal) object[7]);
        orderReponse.setMoneyReduce((BigDecimal) object[8]);
        orderReponse.setTotalMoney((BigDecimal) object[9]);
        orderReponse.setCreatedDate((Date) object[10]);
        orderReponse.setPayDate((Date) object[11]);
        orderReponse.setShipDate((Date) object[12]);
        orderReponse.setDesiredDate((Date) object[13]);
        orderReponse.setReceiveDate((Date) object[14]);
        orderReponse.setCreatedBy((String) object[15]);
        orderReponse.setUpdatedBy((String) object[16]);
        orderReponse.setStatus((Integer) object[17]);
        return orderReponse;
    }

    @Override
    public Page<OrderReponse> searchOrder(SearchOrderRequest searchOrderRequest) {
        Pageable pageable = PageRequest.of(searchOrderRequest.getPage(), searchOrderRequest.getSize());
        if (searchOrderRequest.getCustomer() != null) {
            String customer = searchOrderRequest.getCustomer();
            customer = customer.replaceAll("\\\\", "\\\\\\\\");
            customer = customer.replaceAll("%", "\\\\%");
            customer = customer.replaceAll("_", "\\\\_");
            searchOrderRequest.setCustomer(customer);
        }
        Page<Object> objects = orderCustomRepository.doSearch(
                pageable,
                searchOrderRequest.getType(),
                searchOrderRequest.getVoucher(),
                searchOrderRequest.getCustomer(),
                searchOrderRequest.getDateFirst(),
                searchOrderRequest.getDateLast(),
                searchOrderRequest.getStatus(),
                searchOrderRequest.getPriceMin(),
                searchOrderRequest.getPriceMax()
        );
        List<OrderReponse> list = new ArrayList<>();
        for (Object object : objects) {
            Object[] result = (Object[]) object;
            OrderReponse orderReponse = convertPage(result);
            list.add(orderReponse);
        }
        return new PageImpl<>(list, pageable, objects.getTotalElements());
    }

    @Override
    public Order getOne(String code) {
        return orderRepository.findOrderByCode(code);
    }

    // gen mã tự động
    public String generateOrderCode() {
        List<Order> invoices = orderRepository.findAll();
        int nextInvoiceNumber = invoices.size() + 1;
        return "HD" + String.format("%05d", nextInvoiceNumber);
    }

    @Override
    public ServiceResultReponse<Order> add(OrderRequest orderRequest) {
        try {
            Date date = new Date();
            Order order = new Order();
            order.setCode(generateOrderCode());
            order.setType("Tại quầy");
            order.setCreatedBy(orderRequest.getCreatedBy());
            order.setUpdatedBy(order.getUpdatedBy());
            order.setCreatedDate(date);
            order.setStatus(1);
            Order orderAdd = orderRepository.save(order);
            return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderAdd, "Tạo Order thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Tạo order thất bại");
        }
    }
}
