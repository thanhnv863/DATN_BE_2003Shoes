package com.backend.controller;

import com.backend.JasperService;
import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.OrderRequest;
import com.backend.dto.request.OrderRequetUpdate;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.response.OrderReponse;
import com.backend.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/admin/order")
public class OrderController {

    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private JasperService jasperService;

    @PostMapping("/get-all")
    public ResponseEntity<?> getAllOrder(@RequestBody SearchOrderRequest searchOrderRequest) {
        Page<OrderReponse> page = iOrderService.searchOrder(searchOrderRequest);
        return ResponseEntity.ok().body(new ServiceResultReponse<>(AppConstant.SUCCESS, page.getTotalElements(), page.getContent(), "Lấy danh sách thành công "));
//        return ResponseEntity.ok().body(page);
    }

    @GetMapping("/get-one/{code}")
    public ResponseEntity<?> getOne(@PathVariable("code") String code) {
        return ResponseEntity.ok().body(iOrderService.getOne(code));
    }

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody OrderRequest orderRequest) {
        return ResponseEntity.ok().body(iOrderService.add(orderRequest));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updtate(@RequestBody OrderRequetUpdate orderRequetUpdate) {
        return ResponseEntity.ok().body(iOrderService.update(orderRequetUpdate));
    }

    @PostMapping("/updateInformation")
    public ResponseEntity<?> updtateInformation(@RequestBody OrderRequetUpdate orderRequetUpdate) {
        return ResponseEntity.ok().body(iOrderService.updateInformation(orderRequetUpdate));
    }

    @PostMapping("/updateTien")
    public ResponseEntity<?> updtateTien(@RequestBody OrderRequetUpdate orderRequetUpdate) {
        return ResponseEntity.ok().body(iOrderService.updateTien(orderRequetUpdate));
    }

    @PostMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody OrderRequetUpdate orderRequetUpdate) {
        return ResponseEntity.ok().body(iOrderService.delete(orderRequetUpdate));
    }

    @PostMapping("/get-order-by-status")
    public ResponseEntity<?> getOrderByStatus0() {
        Integer status = 0;
        return ResponseEntity.ok().body(iOrderService.getOrderByStatus(status));
    }

    @PostMapping("/export-order")
    public ResponseEntity<byte[]> exportSubjectsToExcel(HttpServletResponse response, @RequestBody SearchOrderRequest searchOrderRequest) throws IOException {
        byte[] excelData = iOrderService.exportExcelListOrder(searchOrderRequest);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=list-order-file.xlsx");
        return ResponseEntity.ok().body(excelData);
    }

    @GetMapping("/generate-hoa-don-report/{hoaDon}")
    public ResponseEntity<InputStreamResource> generateNhanVienReport(@PathVariable("hoaDon") Long hoaDon) {
        InputStreamResource inputStreamResource = jasperService.generateAndExportNhanVienReport(hoaDon);

        if (inputStreamResource == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "Order_" + hoaDon + ".pdf");
//        headers.setCacheControl("no-cache, no-store, must-revalidate");

        return ResponseEntity.ok()
                .headers(headers)
                .body(inputStreamResource);
    }


}
