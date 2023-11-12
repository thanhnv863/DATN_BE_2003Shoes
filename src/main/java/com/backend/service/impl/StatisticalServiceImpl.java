package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.statistical.DataItem;
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
    public ServiceResult<List<DataItem>> thongKeSanPhamBanChayTrongNam(Integer nam) {
        List<Object[]> getHangHoa = statisticalRepository.findByHangHoaBanChayTrongNam(nam);
        List<HangHoaBanChayTrongNam> result = new ArrayList<>();
        List<DataItem> dataItems = new ArrayList<>();

        for (Object[] record: getHangHoa){
            HangHoaBanChayTrongNam hangHoaBanChayTrongNam = new HangHoaBanChayTrongNam();

            DataItem data1 = new DataItem();
            hangHoaBanChayTrongNam.setValue1("thang1") ;
            data1.setThang(hangHoaBanChayTrongNam.getValue1());
            hangHoaBanChayTrongNam.setThang1((BigDecimal) record[0]);
            data1.setValues(hangHoaBanChayTrongNam.getThang1());

            DataItem data2 = new DataItem();
            hangHoaBanChayTrongNam.setValue2("thang2");
            data2.setThang(hangHoaBanChayTrongNam.getValue2());
            hangHoaBanChayTrongNam.setThang2((BigDecimal) record[1]);
            data2.setValues(hangHoaBanChayTrongNam.getThang2());

            DataItem data3 = new DataItem();
            hangHoaBanChayTrongNam.setValue3("thang3");
            data3.setThang(hangHoaBanChayTrongNam.getValue3());
            hangHoaBanChayTrongNam.setThang3((BigDecimal) record[2]);
            data3.setValues(hangHoaBanChayTrongNam.getThang3());

            DataItem data4 = new DataItem();
            hangHoaBanChayTrongNam.setValue4("thang4");
            data4.setThang(hangHoaBanChayTrongNam.getValue4());
            hangHoaBanChayTrongNam.setThang4((BigDecimal) record[3]);
            data4.setValues(hangHoaBanChayTrongNam.getThang4());

            DataItem data5 = new DataItem();
            hangHoaBanChayTrongNam.setValue5("thang5");
            data5.setThang(hangHoaBanChayTrongNam.getValue5());
            hangHoaBanChayTrongNam.setThang5((BigDecimal) record[4]);
            data5.setValues(hangHoaBanChayTrongNam.getThang5());

            DataItem data6 = new DataItem();
            hangHoaBanChayTrongNam.setValue6("thang6");
            data6.setThang(hangHoaBanChayTrongNam.getValue6());
            hangHoaBanChayTrongNam.setThang6((BigDecimal) record[5]);
            data6.setValues(hangHoaBanChayTrongNam.getThang6());

            DataItem data7 = new DataItem();
            hangHoaBanChayTrongNam.setValue7("thang7");
            data7.setThang(hangHoaBanChayTrongNam.getValue7());
            hangHoaBanChayTrongNam.setThang7((BigDecimal) record[6]);
            data7.setValues(hangHoaBanChayTrongNam.getThang7());

            DataItem data8 = new DataItem();
            hangHoaBanChayTrongNam.setValue8("thang8");
            data8.setThang(hangHoaBanChayTrongNam.getValue8());
            hangHoaBanChayTrongNam.setThang8((BigDecimal) record[7]);
            data8.setValues(hangHoaBanChayTrongNam.getThang8());

            DataItem data9 = new DataItem();
            hangHoaBanChayTrongNam.setValue9("thang9");
            data9.setThang(hangHoaBanChayTrongNam.getValue9());
            hangHoaBanChayTrongNam.setThang9((BigDecimal) record[8]);
            data9.setValues(hangHoaBanChayTrongNam.getThang9());

            DataItem data10 = new DataItem();
            hangHoaBanChayTrongNam.setValue10("thang10");
            data10.setThang(hangHoaBanChayTrongNam.getValue10());
            hangHoaBanChayTrongNam.setThang10((BigDecimal) record[9]);
            data10.setValues(hangHoaBanChayTrongNam.getThang10());

            DataItem data11 = new DataItem();
            hangHoaBanChayTrongNam.setValue11("thang11");
            data11.setThang(hangHoaBanChayTrongNam.getValue11());
            hangHoaBanChayTrongNam.setThang11((BigDecimal) record[10]);
            data11.setValues(hangHoaBanChayTrongNam.getThang11());

            DataItem data12 = new DataItem();
            hangHoaBanChayTrongNam.setValue12("thang12");
            data12.setThang(hangHoaBanChayTrongNam.getValue12());
            hangHoaBanChayTrongNam.setThang12((BigDecimal) record[11]);
            data12.setValues(hangHoaBanChayTrongNam.getThang12());

            result.add(hangHoaBanChayTrongNam);
            dataItems.add(data1);
            dataItems.add(data2);
            dataItems.add(data3);
            dataItems.add(data4);
            dataItems.add(data5);
            dataItems.add(data6);
            dataItems.add(data7);
            dataItems.add(data8);
            dataItems.add(data9);
            dataItems.add(data10);
            dataItems.add(data11);
            dataItems.add(data12);
        }

        int currentYear = LocalDate.now().getYear();
        if(nam > currentYear){
            return new ServiceResult<>(AppConstant.NOT_FOUND,"chua den nam nay",null);
        }

        if(result.size() > 0){
            return new ServiceResult<>(AppConstant.SUCCESS,"get success",dataItems);
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
