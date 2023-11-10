package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.SearchPaymentMethod;
import com.backend.dto.request.paymentMethod.PaymentMethodRequest;
import com.backend.dto.request.paymentMethod.PaymentMethodRequestUpdate;
import com.backend.dto.response.PaymentMethodReponse;
import com.backend.entity.Order;
import com.backend.entity.PaymentMethod;
import com.backend.repository.OrderRepository;
import com.backend.repository.PaymentMethodCustomRepository;
import com.backend.repository.PaymentMethodRepository;
import com.backend.service.IPaymentMethodService;
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
import java.util.Optional;

@Service
public class PaymentMethodServiceImpl implements IPaymentMethodService {
    @Autowired
    private PaymentMethodCustomRepository paymentMethodCustomRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodReponse convertPage(Object[] object) {
        PaymentMethodReponse paymentMethodReponse = new PaymentMethodReponse();
        paymentMethodReponse.setId(((BigInteger) object[0]).longValue());
        paymentMethodReponse.setCode((String) object[1]);
        paymentMethodReponse.setMethod((String) object[2]);
        paymentMethodReponse.setTotal((BigDecimal) object[3]);
        paymentMethodReponse.setPaymentTime((Date) object[4]);
        paymentMethodReponse.setNote((String) object[5]);
        paymentMethodReponse.setStatus((Integer) object[6]);
        return paymentMethodReponse;
    }

    @Override
    public Page<PaymentMethodReponse> searchPaymentMethod(SearchPaymentMethod searchPaymentMethod) {
        Pageable pageable = PageRequest.of(searchPaymentMethod.getPage(), searchPaymentMethod.getSize());
        Page<Object> objects = paymentMethodCustomRepository.doSearch(
                pageable,
                searchPaymentMethod.getOrderCode(),
                searchPaymentMethod.getMethod(),
                searchPaymentMethod.getPriceMin(),
                searchPaymentMethod.getPriceMax(),
                searchPaymentMethod.getDateFirst(),
                searchPaymentMethod.getDateLast(),
                searchPaymentMethod.getStatus()
        );
        List<PaymentMethodReponse> list = new ArrayList<>();
        for (Object object : objects) {
            Object[] result = (Object[]) object;
            PaymentMethodReponse paymentMethodReponse = convertPage(result);
            list.add(paymentMethodReponse);
        }
        return new PageImpl<>(list, pageable, objects.getTotalElements());
    }

    @Override
    public ServiceResultReponse<PaymentMethod> add(PaymentMethodRequest paymentMethodRequest) {
        try {
            Optional<Order> order = orderRepository.findById(paymentMethodRequest.getOrderId());
            Date date = new Date();
            if (order.isPresent()) {
                Order order1 = order.get();
                PaymentMethod paymentMethod = new PaymentMethod();
                paymentMethod.setOrder(order1);
                paymentMethod.setMethod(paymentMethodRequest.getMethod());
                paymentMethod.setTotal(paymentMethodRequest.getTotal());
                paymentMethod.setPaymentTime(date);
                paymentMethod.setNote(paymentMethodRequest.getNote());
                paymentMethod.setStatus(paymentMethodRequest.getStatus());
                PaymentMethod paymentMethodAdd = paymentMethodRepository.save(paymentMethod);
                return new ServiceResultReponse<>(AppConstant.FAIL, 1L, paymentMethodAdd, "Tạo phương thức thanh toán với mã hóa đơn " + paymentMethodRequest.getOrderId() + " thành công");
            } else {
                return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Không tồn tại hóa đơn");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Tạo phương thức thanh toán với mã hóa đơn" + paymentMethodRequest.getOrderId() + " thất bại");
        }
    }

    @Override
    public ServiceResultReponse<PaymentMethod> update(PaymentMethodRequestUpdate paymentMethodRequestUpdate) {
        try {
            if (paymentMethodRequestUpdate.getId() != null) {
                Optional<PaymentMethod> paymentMethod = paymentMethodRepository.findById(paymentMethodRequestUpdate.getId());
                if (paymentMethod.isPresent()) {
                    PaymentMethod paymentMethodUpdate = paymentMethod.get();
                    Optional<Order> order = orderRepository.findById(paymentMethodRequestUpdate.getOrderId());
                    if (order.isPresent()) {
                        Order order1 = order.get();
                        paymentMethodUpdate.setOrder(order1);
                        paymentMethodUpdate.setMethod(paymentMethodRequestUpdate.getMethod());
                        paymentMethodUpdate.setTotal(paymentMethodRequestUpdate.getTotal());
                        paymentMethodUpdate.setNote(paymentMethodRequestUpdate.getNote());
                        paymentMethodUpdate.setStatus(paymentMethodRequestUpdate.getStatus());
                        PaymentMethod paymentMethodUpdate1 = paymentMethodRepository.save(paymentMethodUpdate);
                        return new ServiceResultReponse<>(AppConstant.FAIL, 1L, paymentMethodUpdate1, "Tạo phương thức thanh toán với mã hóa đơn: " + paymentMethodRequestUpdate.getOrderId() + " thành công");
                    } else {
                        return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Không tồn tại mã hóa đơn: " + paymentMethodRequestUpdate.getOrderId() + " ");
                    }
                } else {
                    return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Id paymentMethod: " + paymentMethodRequestUpdate.getId() + " không tồn tại");
                }
            } else {
                return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Vui lòng nhập Id của paymentMethod");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Cập nhật phương thức thanh toán thất bại");
        }
    }

    @Override
    public ServiceResult<?> getAllByIdOrder(Long idOrder) {
        Order order = orderRepository.findById(idOrder).get();
        List<PaymentMethod> paymentMethodList = paymentMethodRepository.findAllByAndOrder(order);
        return new ServiceResult<>(AppConstant.SUCCESS, "Lấy danh sách thành công", paymentMethodList);
    }
}
