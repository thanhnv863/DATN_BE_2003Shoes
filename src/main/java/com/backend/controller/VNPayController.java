package com.backend.controller;

import com.backend.dto.request.VNPayRequest;
import com.backend.service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
public class VNPayController {
    @Autowired
    private VNPayService vnPayService;

    @PostMapping("/submitOrder")
    public String submidOrder(@RequestBody VNPayRequest vnPayRequest,
                              HttpServletRequest request) {
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnPayService.createOrder(vnPayRequest.getOrderTotal(), vnPayRequest.getOrderInfo(), baseUrl);
        return vnpayUrl;
    }

    @GetMapping("/vnpay-payment")
    public void GetMapping(HttpServletRequest request, Model model, HttpServletResponse response) throws IOException {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");
//
//        model.addAttribute("orderId", orderInfo);
//        model.addAttribute("totalPrice", totalPrice);
//        model.addAttribute("paymentTime", paymentTime);
//        model.addAttribute("transactionId", transactionId);

        if (paymentStatus == 1){
            String redirectUrl = "http://localhost:3000/order-checking" +
                    "?orderId=" + URLEncoder.encode(orderInfo, StandardCharsets.UTF_8.toString()) +
                    "&totalPrice=" + URLEncoder.encode(totalPrice, StandardCharsets.UTF_8.toString()) +
                    "&paymentTime=" + URLEncoder.encode(paymentTime, StandardCharsets.UTF_8.toString()) +
                    "&transactionId=" + URLEncoder.encode(transactionId, StandardCharsets.UTF_8.toString());

            response.sendRedirect(redirectUrl);
        }else {
            response.sendRedirect( "http://localhost:3000/order-checking?transactionId=0");
        }
//
    }
}