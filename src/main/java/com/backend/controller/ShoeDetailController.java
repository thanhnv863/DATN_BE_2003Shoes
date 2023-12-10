package com.backend.controller;

import com.backend.ServiceResult;
import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.request.ShoeDetailRequestUpdate;
import com.backend.dto.request.shoedetail.ListSizeOfShoeReq;
import com.backend.dto.request.shoedetail.SearchShoeDetailRequest;
import com.backend.dto.request.shoedetail.ShoeDetailId;
import com.backend.dto.request.shoedetail.ShoeDetailRequest;
import com.backend.dto.request.size.SizeRequestUpdate;
import com.backend.dto.response.OrderReponse;
import com.backend.dto.response.ResponseImport;
import com.backend.dto.response.shoedetail.ResultItem;
import com.backend.service.IShoeDetailService;
import com.backend.service.IShoeService;
import com.backend.service.impl.ShoeDetailServiceImpl;
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
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/shoe-detail")
public class ShoeDetailController {
    @Autowired
    private IShoeService iShoeService;

    @Autowired
    private IShoeDetailService iShoeDetailService;

    @Autowired
    private ShoeDetailServiceImpl shoeDetailServiceimpl;

    @GetMapping("/getAllShoeDetailWithPaginate")
    public ResponseEntity<?> getAllShoeDetailWithPaginate(@RequestParam(defaultValue = "0") Integer page,
                                                          @RequestParam(defaultValue = "2") Integer pageSize,
                                                          @RequestParam(name = "nameShoe",required = false) String name,
                                                          @RequestParam(name = "sizeShoe",required = false) Float size,
                                                          @RequestParam(name = "brandShoe",required = false) String brand
    ){
        return ResponseEntity.ok(iShoeService.getAllShoeItemstest(page,pageSize,name,size,brand));
    }

    @PostMapping("/addNewShoeDetail")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> addNewShoe(@RequestBody ShoeDetailRequest shoeDetailRequest){
        return ResponseEntity.ok(iShoeDetailService.addNewShoe(shoeDetailRequest));
    }

    @PostMapping("/updateShoeDetail")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updateShoeDetail(@RequestBody ShoeDetailRequestUpdate shoeDetailRequestUpdate){
        return ResponseEntity.ok(iShoeDetailService.updateShoeDetail(shoeDetailRequestUpdate));
    }

    @PostMapping("/inActiveShoeDetail")
    public ResponseEntity<?> inActiveShoeDetail(@RequestBody ShoeDetailId shoeDetailId){
        return ResponseEntity.ok(iShoeDetailService.inActiveShoeDetail(shoeDetailId));
    }

    @PostMapping("/activeShoeDetail")
    public ResponseEntity<?> activeShoeDetail(@RequestBody ShoeDetailId shoeDetailId){
        return ResponseEntity.ok(iShoeDetailService.activeShoeDetail(shoeDetailId));
    }

    @PostMapping("/getAllHomePage")
    public ResponseEntity<?> getAllShoeDetail(@RequestBody SearchShoeDetailRequest searchShoeDetailRequest) {
        Page<ResultItem> page = iShoeDetailService.searchShoeDetail(searchShoeDetailRequest);
        return ResponseEntity.ok().body(
                new ServiceResultReponse<>(
                        AppConstant.SUCCESS,
                        page.getTotalElements(),
                        page.getContent(), "Lấy danh sách thành công "
                )
        );
    }

    @GetMapping("/getShoeDetailById/{id}")
    public ResponseEntity<?> getShoeDetailById(@PathVariable(name = "id")BigInteger id){
        Object response = iShoeDetailService.searchById(id);
        return ResponseEntity.ok().body(new ServiceResult<>(AppConstant.SUCCESS, "Succesfully",response));
    }

    @GetMapping("/getListTop4BestSales")
    public ResponseEntity<?> getListTop4BestSales(){
        List<ResultItem> list = iShoeDetailService.getTop4BestSale();
        return ResponseEntity.ok().body(new ServiceResult<>(AppConstant.SUCCESS, "Succesfully",list));
    }

    @GetMapping("/getListTop4News")
    public ResponseEntity<?> getListTop4News(){
        List<ResultItem> list = iShoeDetailService.getTop4News();
        return ResponseEntity.ok().body(new ServiceResult<>(AppConstant.SUCCESS, "Succesfully",list));
    }

    @GetMapping("/getListVersionOfShoe/{id}")
    public ResponseEntity<?> getListVersionOfShoe(@PathVariable(name = "id") Long id){
        List<ResultItem> list = iShoeDetailService.getVersionOfShoe(id);
        return ResponseEntity.ok().body(new ServiceResult<>(AppConstant.SUCCESS, "Succesfully",list));
    }

    @PostMapping("/getListSizeOfShoeById")
    public ResponseEntity<?> getListSizeOfShoeById(@RequestBody ListSizeOfShoeReq listSizeOfShoeReq){
        return ResponseEntity.ok().body(new ServiceResult<>(AppConstant.SUCCESS, "Succesfully",iShoeDetailService.getListSizeOfShoe(listSizeOfShoeReq)));
    }

    @PostMapping("/getListSizeExist")
    public ResponseEntity<?> getListSizeExist(@RequestBody ListSizeOfShoeReq listSizeOfShoeReq){
        return ResponseEntity.ok().body(new ServiceResult<>(AppConstant.SUCCESS, "Succesfully",iShoeDetailService.getListSizeExits(listSizeOfShoeReq)));
    }

    @PostMapping("/getAllCustom")
    public ResponseEntity<?> getAllCustom(@RequestBody SearchShoeDetailRequest searchShoeDetailRequest) {
        List<ResultItem> list = iShoeDetailService.getShoeDetailsCustom(searchShoeDetailRequest);
        return ResponseEntity.ok().body(
                new ServiceResult<>(
                        AppConstant.SUCCESS,
                        "Lấy danh sách thành công ",
                        list.stream().collect(Collectors.toList())
                )
        );
    }

    @PostMapping("/updateQtyShoeDetail")
    public ResponseEntity<?> updateQtyShoeDetail(@RequestBody List<ShoeDetailRequestUpdate> shoeDetailRequestUpdate){
        return ResponseEntity.ok(iShoeDetailService.updateQtyShoeDetail(shoeDetailRequestUpdate));
    }


    @GetMapping("/download-excel-template")
    public ResponseEntity<byte[]> downloadExcelTemplate(HttpServletResponse response) throws IOException {
        byte[] excelBytes = iShoeDetailService.createExcelFile();
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=TemplateImportSanPhamFile.xlsx");
        return ResponseEntity.ok().body(excelBytes);
    }


    @PostMapping("/import")
    public ResponseEntity<?> importData(@RequestParam("file") MultipartFile file, @RequestParam("type") Integer type) {
        try {
            ResponseImport reponseImport = iShoeDetailService.importDataFromExcel(file, type);
            return ResponseEntity.ok(reponseImport);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi nhập dữ liệu từ Excel");
        }
    }

    @GetMapping("/download-excel-file-error")
    public ResponseEntity<byte[]> exportExcelFileError(HttpServletResponse response) throws IOException {
        byte[] excelBytes = iShoeDetailService.exportExcelFileError();
        // Thiết lập các thông số cho response
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=error-file-product.xlsx");
        return ResponseEntity.ok().body(excelBytes);
    }
}
