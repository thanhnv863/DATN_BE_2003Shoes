package com.backend.controller;

import com.backend.dto.request.ShoeDetailRequest;
import com.backend.dto.request.ShoeRequest;
import com.backend.service.IShoeDetailService;
import com.backend.service.IShoeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/shoe-detail")
public class ShoeDetailController {
    @Autowired
    private IShoeService iShoeService;

    @Autowired
    private IShoeDetailService iShoeDetailService;

    @GetMapping("/getAllShoeDetailWithPaginate")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
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

}
