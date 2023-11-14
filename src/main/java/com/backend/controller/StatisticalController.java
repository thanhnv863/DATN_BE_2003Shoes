package com.backend.controller;

import com.backend.service.IStatistical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/statistical")
public class StatisticalController {

    @Autowired
    private IStatistical iStatistical;

    @GetMapping("/hoadoncho")
    public ResponseEntity<?> getHoaDonHuy(@RequestParam("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBatDau,
                                          @RequestParam("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayKetThuc){
        return ResponseEntity.ok(iStatistical.findHoaDon( ngayBatDau,  ngayKetThuc));
    }

    @GetMapping("/sanphambanchaytrongnam")
    public ResponseEntity<?> getHangHoa(@RequestParam("nam") Integer nam){
        return ResponseEntity.ok(iStatistical.thongKeSanPhamBanChayTrongNam(nam));
    }

    @GetMapping("/top5SanPhamBanChay")
    public ResponseEntity<?> top5SanPhamBanChay(@RequestParam("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBatDau,
                                                @RequestParam("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayKetThuc){
        return ResponseEntity.ok(iStatistical.sanPhamBanChay(ngayBatDau, ngayKetThuc));
    }

}
