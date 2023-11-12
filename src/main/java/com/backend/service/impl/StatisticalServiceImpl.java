package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.statistical.DoanhThuTrongNgay;
import com.backend.dto.statistical.DoanhThuTrongThang;
import com.backend.dto.statistical.HangHoaBanChayTrongNam;
import com.backend.dto.statistical.HoaDonHuy;
import com.backend.repository.StatisticalRepository;
import com.backend.service.IStatistical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticalServiceImpl implements IStatistical {

    @Autowired
    private StatisticalRepository statisticalRepository;

    @Override
    public ServiceResult<List<HoaDonHuy>> findHoaDonHuy(Integer ngayTao, Integer trangThai) {
        List<Object[]> soHoaDonHuy = statisticalRepository.findByHoaDonHuy(ngayTao,trangThai);
        List<HoaDonHuy> hoaDonHuyList = new ArrayList<>();
        for(Object[] hoaDon: soHoaDonHuy){
            HoaDonHuy hoaDonHuy = new HoaDonHuy();
            hoaDonHuy.setNgay((BigInteger) hoaDon[0]);
            hoaDonHuy.setSoHoaDonHuy((BigInteger) hoaDon[1]);
            hoaDonHuyList.add(hoaDonHuy);
        }

        if (hoaDonHuyList.size() > 0){
            return new ServiceResult<>(AppConstant.SUCCESS,"get success",hoaDonHuyList);
        }else{
            return new ServiceResult<>(AppConstant.NOT_FOUND,"khong co hoa don",null);
        }


    }

    @Override
    public ServiceResult<List<HangHoaBanChayTrongNam>> thongKeSanPhamBanChayTrongNam(Integer nam) {
        List<Object[]> getHangHoa = statisticalRepository.findByHangHoaBanChayTrongNam(nam);
        List<HangHoaBanChayTrongNam> result = new ArrayList<>();
        for (Object[] record: getHangHoa){
            HangHoaBanChayTrongNam hangHoaBanChayTrongNam = new HangHoaBanChayTrongNam();

            hangHoaBanChayTrongNam.setValue1("thang1") ;
            hangHoaBanChayTrongNam.setThang1((BigDecimal) record[0]);

            hangHoaBanChayTrongNam.setValue2("thang2");
            hangHoaBanChayTrongNam.setThang2((BigDecimal) record[1]);

            hangHoaBanChayTrongNam.setValue3("thang3");
            hangHoaBanChayTrongNam.setThang3((BigDecimal) record[2]);

            hangHoaBanChayTrongNam.setValue4("thang4");
            hangHoaBanChayTrongNam.setThang4((BigDecimal) record[3]);

            hangHoaBanChayTrongNam.setValue5("thang5");
            hangHoaBanChayTrongNam.setThang5((BigDecimal) record[4]);

            hangHoaBanChayTrongNam.setValue6("thang6");
            hangHoaBanChayTrongNam.setThang6((BigDecimal) record[5]);

            hangHoaBanChayTrongNam.setValue7("thang7");
            hangHoaBanChayTrongNam.setThang7((BigDecimal) record[6]);

            hangHoaBanChayTrongNam.setValue8("thang8");
            hangHoaBanChayTrongNam.setThang8((BigDecimal) record[7]);

            hangHoaBanChayTrongNam.setValue9("thang9");
            hangHoaBanChayTrongNam.setThang9((BigDecimal) record[8]);

            hangHoaBanChayTrongNam.setValue10("thang10");
            hangHoaBanChayTrongNam.setThang10((BigDecimal) record[9]);

            hangHoaBanChayTrongNam.setValue11("thang11");
            hangHoaBanChayTrongNam.setThang11((BigDecimal) record[10]);

            hangHoaBanChayTrongNam.setValue12("thang12");
            hangHoaBanChayTrongNam.setThang12((BigDecimal) record[11]);

            hangHoaBanChayTrongNam.setTongSoLuong((BigDecimal) record[12]);

            result.add(hangHoaBanChayTrongNam);
        }

        int currentYear = LocalDate.now().getYear();
        if(nam > currentYear){
            return new ServiceResult<>(AppConstant.NOT_FOUND,"chua den nam nay",null);
        }

        if(result.size() > 0){
            return new ServiceResult<>(AppConstant.SUCCESS,"get success",result);
        }else{
            return new ServiceResult<>(AppConstant.NOT_FOUND,"khong co san pham nao",null);
        }
    }

    @Override
    public ServiceResult<List<DoanhThuTrongNgay>> doanhThuTrongNgay(Integer ngay) {
        List<Object[]> getHangHoa = statisticalRepository.doanhThuTrongNgay(ngay);
        List<DoanhThuTrongNgay> result = new ArrayList<>();

        for(Object[] record: getHangHoa){
            DoanhThuTrongNgay doanhThuTrongNgay = new DoanhThuTrongNgay();
            doanhThuTrongNgay.setNgay((BigInteger) record[0]);
            doanhThuTrongNgay.setTongTien((BigDecimal) record[1] == null ? BigDecimal.valueOf(0)  : (BigDecimal) record[1]);
            result.add(doanhThuTrongNgay);
        }

        return new ServiceResult<>(AppConstant.SUCCESS,"get success",result);
    }

    @Override
    public ServiceResult<List<DoanhThuTrongThang>> doanhThuTrongThang(Integer thang) {
        List<Object[]> getHangHoa = statisticalRepository.doanhThuTrongThang(thang);
        List<DoanhThuTrongThang> result = new ArrayList<>();

        for(Object[] record: getHangHoa){
            DoanhThuTrongThang doanhThuTrongThang = new DoanhThuTrongThang();
            doanhThuTrongThang.setThang((BigInteger) record[0]);
            doanhThuTrongThang.setTongTien((BigDecimal) record[1] == null ? BigDecimal.valueOf(0)  : (BigDecimal) record[1]);
            result.add(doanhThuTrongThang);
        }

        return new ServiceResult<>(AppConstant.SUCCESS,"get success",result);
    }


}
