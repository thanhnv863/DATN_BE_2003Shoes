package com.backend.service;

import com.backend.ServiceResult;
import com.backend.dto.statistical.DataItem;
import com.backend.dto.statistical.Top5SanPhamBanChayTrongThangVaNam;

import java.util.Date;
import java.util.List;

public interface IStatistical {
    ServiceResult<List<DataItem>> findHoaDon(Date ngayBatDau,Date ngayKetThuc);
    ServiceResult<List<DataItem>> thongKeSanPhamBanChayTrongNam(Integer nam);
    ServiceResult<List<Top5SanPhamBanChayTrongThangVaNam>>  sanPhamBanChay(Date ngayBatDau,Date ngayKetThuc);
}
