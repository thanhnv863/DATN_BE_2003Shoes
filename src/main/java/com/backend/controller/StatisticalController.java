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
    public ResponseEntity<?> top5SanPhamBanChay(){
        return ResponseEntity.ok(iStatistical.sanPhamBanChay());
    }

    @GetMapping("/thongkedoanhthu")
    public ResponseEntity<?> thongKeDoanhTHu(@RequestParam("ngayBatDau") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayBatDau,
                                             @RequestParam("ngayKetThuc") @DateTimeFormat(pattern = "yyyy-MM-dd") Date ngayKetThuc,
                                             @RequestParam("typeBanHang") Integer typeBanHang){
        return ResponseEntity.ok(iStatistical.thongKeDoanhThu(ngayBatDau, ngayKetThuc, typeBanHang));
    }

    @GetMapping("/doanhthutheongay")
    public ResponseEntity<?> doanhThuTheoNgay(@RequestParam(value = "typeBanHang",defaultValue = "") Integer typeBanHang){
        return ResponseEntity.ok(iStatistical.doanhThuTheoNgay(typeBanHang));
    }

    @GetMapping("/doanhthutheothang")
    public ResponseEntity<?> doanhThuTheoThang(@RequestParam("nam") Integer nam,
                                               @RequestParam(value = "typeBanHang",defaultValue = "") Integer typeBanHang){
        return ResponseEntity.ok(iStatistical.doanhThuTheoThang(nam, typeBanHang));
    }

    @GetMapping("/so-luong-hang-hoa-ban-theo-nam")
    public ResponseEntity<?> soHangHoaTheoNam(@RequestParam("nam") Integer nam){
        return ResponseEntity.ok(iStatistical.soHangHoaBanDuocTrongNam(nam));
    }

    @GetMapping("/so-luong-hoa-don-theo-ngay")
    public ResponseEntity<?> soLuongHangHoaTrongNgay(@RequestParam(value = "typeBanHang",defaultValue = "") Integer typeBanHang){
        return ResponseEntity.ok(iStatistical.soHoaDonBanTrongNgay(typeBanHang));
    }

    @GetMapping("/so-luong-hoa-don-theo-thang")
    public ResponseEntity<?> soLuongHangHoaTrongThang(@RequestParam(value = "typeBanHang",defaultValue = "") Integer typeBanHang){
        return ResponseEntity.ok(iStatistical.soHoaDonBanTrongThang(typeBanHang));
    }

    @GetMapping("/so-luong-hoa-don-theo-nam")
    public ResponseEntity<?> soLuongHangHoaTrongNam(@RequestParam(value = "typeBanHang",defaultValue = "") Integer typeBanHang){
        return ResponseEntity.ok(iStatistical.soHoaDonBanTrongNam(typeBanHang));
    }
}
