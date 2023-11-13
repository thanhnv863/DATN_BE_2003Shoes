package com.backend.controller;

import com.backend.dto.statistical.HoaDonHuy;
import com.backend.service.IStatistical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statistical")
public class StatisticalController {

    @Autowired
    private IStatistical iStatistical;

//    public ResponseEntity<?> addressName(@RequestParam("name") String name){
//        return ResponseEntity.ok(iAddressService.searchNameClient(name));
//    }

    @GetMapping("/hoadoncho")
    public ResponseEntity<?> getHoaDonHuy(@RequestParam("thang") Integer thang){
        return ResponseEntity.ok(iStatistical.findHoaDon(thang));
    }

    @GetMapping("/sanphambanchaytrongnam")
    public ResponseEntity<?> getHangHoa(@RequestParam("nam") Integer nam){
        return ResponseEntity.ok(iStatistical.thongKeSanPhamBanChayTrongNam(nam));
    }

    @GetMapping("/doanhthutrongngay")
    public ResponseEntity<?> getDoanhThuTrongNgay(@RequestParam("ngay") Integer ngay){
        return ResponseEntity.ok(iStatistical.doanhThuTrongNgay(ngay));
    }

    @GetMapping("/doanhthutrongthang")
    public ResponseEntity<?> getDoanhThuTrongThang(@RequestParam("thang") Integer thang){
        return ResponseEntity.ok(iStatistical.doanhThuTrongThang(thang));
    }
}
