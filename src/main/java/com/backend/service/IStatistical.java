package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.statistical.DataItem;
import com.backend.dto.statistical.DataItemDoanhThu;
import com.backend.dto.statistical.DataItemDoanhThuThang;
import com.backend.dto.statistical.DoanhThuTheoNgay;
import com.backend.dto.statistical.DoanhThuTheoThang;
import com.backend.dto.statistical.Top5SanPhamBanChayTrongThangVaNam;

import java.util.Date;
import java.util.List;

public interface IStatistical {
    ServiceResult<List<DataItem>> findHoaDon(Date ngayBatDau,Date ngayKetThuc);
    ServiceResult<List<DataItem>> thongKeSanPhamBanChayTrongNam(Integer nam);
    ServiceResult<List<Top5SanPhamBanChayTrongThangVaNam>>  sanPhamBanChay(Date ngayBatDau,Date ngayKetThuc);
    ServiceResult<List<DataItemDoanhThu>> thongKeDoanhThu(Date ngayBatDau, Date ngayKetThuc, Integer typeBanHang);
    ServiceResult<List<DoanhThuTheoNgay>> doanhThuTheoNgay( Integer typeBanHang);
    ServiceResult<List<DataItemDoanhThuThang>> doanhThuTheoThang(Integer nam, Integer typeBanHang);
}
