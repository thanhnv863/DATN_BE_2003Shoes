package com.backend.service.impl;

import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.OrderRequest;
import com.backend.dto.request.OrderRequetUpdate;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.response.OrderReponse;
import com.backend.entity.Account;
import com.backend.entity.Order;
import com.backend.entity.OrderHistory;
import com.backend.entity.VoucherOrder;
import com.backend.repository.AccountRepository;
import com.backend.repository.OrderCustomRepository;
import com.backend.repository.OrderHistoryRepository;
import com.backend.repository.OrderRepository;
import com.backend.repository.VoucherOrderRepository;
import com.backend.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderCustomRepository orderCustomRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private VoucherOrderRepository voucherOrderRepository;


    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private AccountRepository accountRepository;

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
        orderReponse.setNote((String) object[17]);
        orderReponse.setStatus((Integer) object[18]);
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
    public ServiceResultReponse<Order> getOne(String code) {
        Optional<Order> order = orderRepository.findOrderByCode(code);
        if (order.isPresent()) {
            Order orderGet = order.get();
            return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderGet, "Đã tìm thấy order");
        } else {
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Mã order không tồn tại");

        }
    }

    // gen mã tự động
    public String generateOrderCode() {
        List<Order> invoices = orderRepository.findAll();
        int nextInvoiceNumber = invoices.size() + 1;
        return "HD" + String.format("%05d", nextInvoiceNumber);
    }

    @Override
    public ServiceResultReponse<Order> add(OrderRequest orderRequest) {
        List<Object> objectList = orderRepository.listOrderByStatus(1);
        if (!objectList.isEmpty() && objectList.size() >= 5) {
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Chỉ được tạo tối đa 5 hóa đơn chờ! ");
        } else {
            try {
                Date date = new Date();
                Order order = new Order();
                order.setCode(generateOrderCode());
                order.setCreatedBy(orderRequest.getCreatedBy());
//            order.setUpdatedBy(order.getUpdatedBy());
                order.setCreatedDate(date);
                order.setStatus(1);
                Order orderAdd = orderRepository.save(order);
                //
                OrderHistory orderHistory = new OrderHistory();
                orderHistory.setOrder(orderAdd);
                orderHistory.setCreatedTime(date);
                orderHistory.setCreatedBy(orderRequest.getCreatedBy());
                orderHistory.setNote("Nhân viên tạo đơn cho khách");
                orderHistory.setType("Created");
                orderHistoryRepository.save(orderHistory);
                //
                return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderAdd, "Tạo hóa đơn thành công");
            } catch (Exception e) {
                e.printStackTrace();
                return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Tạo hoá đơn thất bại");
            }
        }
    }

    @Override
    @Transactional
    public ServiceResultReponse<Order> update(OrderRequetUpdate orderRequetUpdate) {
        Optional<Order> order = orderRepository.findOrderByCode(orderRequetUpdate.getCode());
        Date date = new Date();
        if (order.isPresent()) {
            Order orderGet = order.get();
            orderGet.setId(orderGet.getId());
            orderGet.setCode(orderGet.getCode());
            // voucher
            if (orderRequetUpdate.getIdVoucher() != null) {
                VoucherOrder voucherOrder = voucherOrderRepository.findById(orderRequetUpdate.getIdVoucher()).get();
                orderGet.setVoucherOrder(voucherOrder);
            } else {
                orderGet.setVoucherOrder(null);
            }
            //
            if (orderRequetUpdate.getIdAccount() != null) {
                Account account = accountRepository.findById(orderRequetUpdate.getIdAccount()).get();
                orderGet.setAccount(account);
            } else {
                orderGet.setAccount(null);
            }
            //
            orderGet.setType(orderRequetUpdate.getType());
            orderGet.setId(orderGet.getId());
            orderGet.setCustomerName(orderRequetUpdate.getCustomerName());
            orderGet.setPhoneNumber(orderRequetUpdate.getPhoneNumber());
            orderGet.setCustomerName(orderRequetUpdate.getCustomerName());
            orderGet.setAddress(orderRequetUpdate.getAddress());
            orderGet.setShipFee(orderRequetUpdate.getShipFee());
            orderGet.setMoneyReduce(orderRequetUpdate.getMoneyReduce());
            orderGet.setTotalMoney(orderRequetUpdate.getTotalMoney());
            orderGet.setCreatedDate(orderGet.getCreatedDate());
            orderGet.setPayDate(orderRequetUpdate.getPayDate());
            orderGet.setShipDate(orderRequetUpdate.getShipDate());
            orderGet.setDesiredDate(orderRequetUpdate.getDesiredDate());
            orderGet.setReceiveDate(orderRequetUpdate.getReceiveDate());
            orderGet.setCreatedBy(orderGet.getCreatedBy());
            orderGet.setUpdatedBy(orderRequetUpdate.getUpdatedBy());
            orderGet.setNote(orderRequetUpdate.getNote());
            orderGet.setStatus(orderRequetUpdate.getStatus());
            Order orderUpdate = orderRepository.save(orderGet);
            //
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setOrder(orderUpdate);
            orderHistory.setCreatedTime(date);
            orderHistory.setCreatedBy(orderRequetUpdate.getUpdatedBy());
            orderHistory.setNote(orderRequetUpdate.getNote());
            orderHistory.setType("Updated");
            orderHistoryRepository.save(orderHistory);
            //
            return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, orderUpdate, "Cập nhật hóa đơn thành công");
        } else {
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Mã hóa đơn không tồn tại!");
        }
    }

    @Override
    public ServiceResultReponse<Order> delete(OrderRequetUpdate orderRequetUpdate) {
        Date date = new Date();
        Optional<Order> order = orderRepository.findOrderByCode(orderRequetUpdate.getCode());
        if (order.isPresent()) {
            Order orderGet = order.get();
            orderGet.setUpdatedBy(orderRequetUpdate.getUpdatedBy());
            orderGet.setNote(orderRequetUpdate.getNote());
            orderGet.setStatus(orderRequetUpdate.getStatus());
            Order orderUpdate = orderRepository.save(orderGet);
            //
            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setOrder(orderUpdate);
            orderHistory.setCreatedTime(date);
            orderHistory.setCreatedBy(orderRequetUpdate.getUpdatedBy());
            orderHistory.setNote(orderRequetUpdate.getNote());
            orderHistory.setType("Canceled");
            orderHistoryRepository.save(orderHistory);
            //
            return new ServiceResultReponse<>(AppConstant.SUCCESS, 0L, orderUpdate, "Hủy hóa đơn thành công");
        } else {
            return new ServiceResultReponse<>(AppConstant.SUCCESS, 0L, null, "Mã không tồn tại");
        }
    }
    @Override
    public ServiceResultReponse<?> getOrderByStatus(Integer status) {
        List<Object> objectList = orderRepository.listOrderByStatus(status);
        List<OrderReponse> list = new ArrayList<>();
        for (Object object : objectList) {
            Object[] result = (Object[]) object;
            OrderReponse orderReponse = convertPage(result);
            list.add(orderReponse);
        }
        return new ServiceResultReponse<>(AppConstant.SUCCESS, Long.valueOf(list.size()), list, "Lấy danh sách hóa đơn chờ thành công!");
    }
}
