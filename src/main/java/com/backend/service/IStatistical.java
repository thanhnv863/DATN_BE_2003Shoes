package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.statistical.DataItem;
import com.backend.dto.statistical.DoanhThuTrongNgay;
import com.backend.dto.statistical.DoanhThuTrongThang;
import com.backend.dto.statistical.HoaDonHuy;

import java.util.List;

public interface IStatistical {
    ServiceResult<List<DataItem>> findHoaDon(Integer thang);
    ServiceResult<List<DataItem>> thongKeSanPhamBanChayTrongNam(Integer nam);
    ServiceResult<List<DoanhThuTrongNgay>> doanhThuTrongNgay(Integer ngay);
    ServiceResult<List<DoanhThuTrongThang>> doanhThuTrongThang(Integer thang);
}
