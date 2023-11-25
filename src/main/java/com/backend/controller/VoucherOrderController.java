package com.backend.controller;

import com.backend.ServiceResult;
import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.request.VoucherOrderRequest;
import com.backend.dto.response.VoucherOrderResponse;
import com.backend.dto.response.ResponseImport;
import com.backend.service.IVoucherOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/voucher-order")
public class VoucherOrderController {

    @Autowired
    private IVoucherOrderService voucherOrderService;

    @PostMapping("/get-all")
    public ResponseEntity<?> getAllVoucherOrder(@RequestBody VoucherOrderRequest voucherOrderRequest) {
        Page<VoucherOrderResponse> page = voucherOrderService.searchVoucher(voucherOrderRequest);
        return ResponseEntity.ok().body(new ServiceResultReponse<>(AppConstant.SUCCESS, page.getTotalElements(), page.getContent(), "Lấy danh sách thành công"));
    }

    @GetMapping("/get-one/{name}")
    public ResponseEntity<?> getOne(@PathVariable("name") String name) {
        return ResponseEntity.ok().body(voucherOrderService.getOne(name));
    }

    @PostMapping("/addNewVoucherOrder")
    public ResponseEntity<?> addVoucherOrder(@RequestBody VoucherOrderRequest voucherOrderRequest) {
        return ResponseEntity.ok(voucherOrderService.addVoucher(voucherOrderRequest));
    }

    @PostMapping("/updateVoucherOrder")
    public ResponseEntity<?> updateVoucherOrder(@RequestBody VoucherOrderRequest voucherOrderRequest) {
        return ResponseEntity.ok(voucherOrderService.updateVoucher(voucherOrderRequest));
    }

    @PostMapping("/updateStatusVoucherOrderCancelFromWait")
    public ResponseEntity<?> updateStatusVoucherOrderCancelFromWait(@RequestBody VoucherOrderRequest voucherOrderRequest) {
        return ResponseEntity.ok(voucherOrderService.updateStatusVoucherCancelFromWait(voucherOrderRequest));
    }

    @PostMapping("/deleteVoucherOrder")
    public ResponseEntity<?> deleteVoucherOrder(@RequestBody VoucherOrderRequest voucherOrderRequest) {
        return ResponseEntity.ok(voucherOrderService.deleteVoucher(voucherOrderRequest));
    }

    @PostMapping("/searchTotalMoneyMyOrder")
    public ResponseEntity<?> searchTotalMoneyMyOrder(@RequestBody VoucherOrderRequest voucherOrderRequest) {
        //return ResponseEntity.ok(voucherOrderService.searchTotalMoneyMyOrder(voucherOrderRequest));
        List<VoucherOrderResponse> list = voucherOrderService.searchTotalMoneyMyOrder(voucherOrderRequest);
        return ResponseEntity.ok().body(new ServiceResult<>(AppConstant.SUCCESS, "Lấy danh sách thành công",list));
    }

    @GetMapping("/download-excel-template")
    public ResponseEntity<byte[]> downloadExcelTemplate(HttpServletResponse response) throws IOException {
        byte[] excelBytes = voucherOrderService.createExcelFile();
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=TemplateImportVoucherFile.xlsx");
        return ResponseEntity.ok().body(excelBytes);
    }


    @PostMapping("/import")
    public ResponseEntity<?> importData(@RequestParam("file") MultipartFile file, @RequestParam("type") Integer type) {
        try {
            ResponseImport voucherResponseImport = voucherOrderService.importDataFromExcel(file, type);
            return ResponseEntity.ok(voucherResponseImport);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi nhập dữ liệu từ Excel");
        }
    }

    @GetMapping("/download-excel-file-error")
    public ResponseEntity<byte[]> exportExcelFileError(HttpServletResponse response) throws IOException {
        byte[] excelBytes = voucherOrderService.exportExcelFileError();
        // Thiết lập các thông số cho response
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=error-file-voucher.xlsx");
        return ResponseEntity.ok().body(excelBytes);
    }


    @PostMapping("/export-voucher")
    public ResponseEntity<byte[]> exportSubjectsToExcel(HttpServletResponse response, @RequestBody VoucherOrderRequest voucherOrderRequest) throws IOException {
        byte[] excelData = voucherOrderService.exportExcelListVoucher(voucherOrderRequest);
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=list-voucher-file.xlsx");
        return ResponseEntity.ok().body(excelData);
    }
}
