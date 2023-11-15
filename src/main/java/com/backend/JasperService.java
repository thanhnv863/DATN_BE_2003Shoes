package com.backend;

import com.backend.dto.response.orderDetail.OrderDetailPDFResponse;
import com.backend.entity.Order;
import com.backend.model.OrderReportData;
import com.backend.repository.OrderDetailRepository;
import com.backend.repository.OrderRepository;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JasperService {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    public InputStreamResource generateAndExportNhanVienReport(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order == null) {
            return null;
        }
        OrderReportData orderReportData = new OrderReportData();
        orderReportData.setOrder(order);

        try {
            InputStream templateStream = getClass().getResourceAsStream("/static/pdf/order.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(templateStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("orderReportData", orderReportData);

            List<OrderDetailPDFResponse> orderDetails = orderDetailRepository.orderDetailPDFByOrderId(order.getId());
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(orderDetails));
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);
            return new InputStreamResource(new ByteArrayInputStream(pdfBytes));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
