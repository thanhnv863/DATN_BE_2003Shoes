package com.backend.controller;

import com.backend.ServiceResult;
import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.SearchOrderRequest;
import com.backend.dto.request.ShoeDetailRequestUpdate;
import com.backend.dto.request.shoedetail.SearchShoeDetailRequest;
import com.backend.dto.request.shoedetail.ShoeDetailRequest;
import com.backend.dto.response.OrderReponse;
import com.backend.dto.response.shoedetail.ResultItem;
import com.backend.service.IShoeDetailService;
import com.backend.service.IShoeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
