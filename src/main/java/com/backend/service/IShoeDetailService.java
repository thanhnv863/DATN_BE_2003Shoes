package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.request.ShoeDetailRequestUpdate;
import com.backend.dto.request.shoedetail.ListSizeOfShoeReq;
import com.backend.dto.request.shoedetail.SearchShoeDetailRequest;
import com.backend.dto.request.shoedetail.ShoeDetailId;
import com.backend.dto.request.shoedetail.ShoeDetailRequest;
import com.backend.dto.response.ResponseImport;
import com.backend.dto.response.ShoeAndShoeDetailResponse;
import com.backend.dto.response.shoedetail.ListSizeOfShoe;
import com.backend.dto.response.shoedetail.ResultItem;
import com.backend.entity.Shoe;
import com.backend.entity.ShoeDetail;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public interface IShoeDetailService {
    ServiceResult<Shoe> addNewShoe(ShoeDetailRequest shoeDetailRequest);

    Page<ResultItem> searchShoeDetail(SearchShoeDetailRequest searchShoeDetailRequest);

    ServiceResult<Shoe> resultValidate(String mess);

    Object searchById(BigInteger id);

    ServiceResult<ShoeDetail> activeShoeDetail(ShoeDetailId shoeDetailId);

    ServiceResult<ShoeDetail> inActiveShoeDetail(ShoeDetailId shoeDetailId);

    List<ListSizeOfShoe> getListSizeOfShoe(ListSizeOfShoeReq listSizeOfShoeReq);

    List<?> getListSizeExits(ListSizeOfShoeReq listSizeOfShoeReq);

    List<ResultItem> getShoeDetailsCustom(SearchShoeDetailRequest searchShoeDetailRequest);

    List<ResultItem> getTop4BestSale();

    List<ResultItem> getTop4News();

    List<ResultItem> getVersionOfShoe(Long idShoe);

    ServiceResult<Shoe> updateShoeDetail(ShoeDetailRequestUpdate shoeDetailRequestUpdate);

    ServiceResult<String> updateQtyShoeDetail(List<ShoeDetailRequestUpdate> shoeDetailRequestUpdateList);

    byte[] createExcelFile() throws IOException;

    ShoeAndShoeDetailResponse createShoeAndShoeDetailResponse(Row row, List<String> errors, List<ShoeAndShoeDetailResponse> shoeAndShoeDetailListError, Integer type);

    void saveShoeAndShoeDetail(ShoeAndShoeDetailResponse shoeAndShoeDetailResponse, Integer type);

    ResponseImport importDataFromExcel(MultipartFile file, Integer type) throws IOException;
    void createErrorExcelFile(String existingExcelFilePath, List<String> errors, List<ShoeAndShoeDetailResponse> shoeAndShoeDetailListError, Integer type);
    byte[] exportExcelFileError() throws IOException;
    CellStyle createHeaderCellStyle(Workbook workbook);

    CellStyle createErrorCellStyle(Workbook workbook);

    CellStyle createErrorCellStyleSymble(Workbook workbook);

    CellStyle createBorderCellStyle(Workbook workbook);
}
