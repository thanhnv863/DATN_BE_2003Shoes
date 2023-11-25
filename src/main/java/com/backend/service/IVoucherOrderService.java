package com.backend.service;

import com.backend.ServiceResult;
import com.backend.ServiceResultReponse;
import com.backend.dto.request.VoucherOrderRequest;
import com.backend.dto.response.VoucherOrderResponse;
import com.backend.dto.response.ResponseImport;
import com.backend.entity.VoucherOrder;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IVoucherOrderService {
    ServiceResult<VoucherOrder> addVoucher(VoucherOrderRequest voucherOrderRequest);

    ServiceResult<VoucherOrder> updateVoucher(VoucherOrderRequest voucherOrderRequest);

    ServiceResult<VoucherOrder> updateStatusVoucherCancelFromWait(VoucherOrderRequest voucherOrderRequest);

    void updateVoucherStatus();

    VoucherOrderResponse convertPage(Object[] object);

    ServiceResult<VoucherOrder> deleteVoucher(VoucherOrderRequest voucherOrderRequest);

    ServiceResult<VoucherOrder> result(String mess);

    String validateVoucher(VoucherOrderRequest voucherOrderRequest);

    Page<VoucherOrderResponse> searchVoucher(VoucherOrderRequest voucherOrderRequest);

    ServiceResultReponse<VoucherOrder> getOne(String code);

    List<VoucherOrderResponse> searchTotalMoneyMyOrder(VoucherOrderRequest voucherOrderRequest);

    byte[] createExcelFile() throws IOException;

    VoucherOrderResponse createVoucherResponse(Row row, List<String> errors, List<VoucherOrderResponse> subjectListError, Integer type);

    void saveVoucherOrder(VoucherOrderResponse voucherOrderResponse, Integer type);

    ResponseImport importDataFromExcel(MultipartFile file, Integer type) throws IOException;

    void createErrorExcelFile(String existingExcelFilePath, List<String> errors, List<VoucherOrderResponse> voucherListError, Integer type);

    byte[] exportExcelFileError() throws IOException;
    CellStyle createHeaderCellStyle(Workbook workbook);

    CellStyle createErrorCellStyle(Workbook workbook);

    CellStyle createErrorCellStyleSymble(Workbook workbook);

    CellStyle createBorderCellStyle(Workbook workbook);

    List<VoucherOrderResponse> searchExportListVoucher(VoucherOrderRequest voucherOrderRequest);

    byte[] exportExcelListVoucher(VoucherOrderRequest voucherOrderRequest) throws IOException;
}
