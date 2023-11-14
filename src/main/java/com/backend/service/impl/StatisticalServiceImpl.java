package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.statistical.DataItem;
import com.backend.dto.statistical.DoanhThuTrongNgay;
import com.backend.dto.statistical.DoanhThuTrongThang;
import com.backend.dto.statistical.SoHangHoaTrongThang;
import com.backend.dto.statistical.HoaDonHuy;
import com.backend.dto.statistical.Top5SanPhamBanChayTrongThangVaNam;
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
    public ServiceResult<List<DataItem>> findHoaDon(Integer thang) {
        List<Object[]> soHoaDonHuy = statisticalRepository.findByHoaDon(thang);
        List<HoaDonHuy> hoaDonHuyList = new ArrayList<>();
        List<DataItem> dataItems = new ArrayList<>();

        for(Object[] hoaDon: soHoaDonHuy){
            HoaDonHuy hoaDonHuy = new HoaDonHuy();

            DataItem data1 = new DataItem();
            data1.setType("hoaDonCho");
            hoaDonHuy.setHoaDonCho((BigDecimal) hoaDon[0]);
            data1.setValues(hoaDonHuy.getHoaDonCho());

            DataItem data2 = new DataItem();
            data2.setType("choThanhToan");
            hoaDonHuy.setChoThanhToan((BigDecimal) hoaDon[1]);
            data2.setValues(hoaDonHuy.getChoThanhToan());

            DataItem data3 = new DataItem();
            data3.setType("daThanhToan");
            hoaDonHuy.setDaThanhToan((BigDecimal) hoaDon[2]);
            data3.setValues(hoaDonHuy.getDaThanhToan());

            DataItem data4 = new DataItem();
            data4.setType("daHuy");
            hoaDonHuy.setDaHuy((BigDecimal) hoaDon[3]);
            data4.setValues(hoaDonHuy.getDaHuy());

            DataItem data5 = new DataItem();
            data5.setType("choXacNhan");
            hoaDonHuy.setChoXacNhan((BigDecimal) hoaDon[4]);
            data5.setValues(hoaDonHuy.getChoXacNhan());

            DataItem data6 = new DataItem();
            data6.setType("daXacNhan");
            hoaDonHuy.setDaXacNhan((BigDecimal) hoaDon[5]);
            data6.setValues(hoaDonHuy.getDaXacNhan());

            DataItem data7 = new DataItem();
            data7.setType("choGiaoHang");
            hoaDonHuy.setChoGiaoHang((BigDecimal) hoaDon[6]);
            data7.setValues(hoaDonHuy.getChoGiaoHang());

            DataItem data8 = new DataItem();
            data8.setType("daBanGiao");
            hoaDonHuy.setDaBanGiao((BigDecimal) hoaDon[7]);
            data8.setValues(hoaDonHuy.getDaBanGiao());

            DataItem data9 = new DataItem();
            data9.setType("hoanThanh");
            hoaDonHuy.setHoanThanh((BigDecimal) hoaDon[8]);
            data9.setValues(hoaDonHuy.getHoanThanh());

            hoaDonHuyList.add(hoaDonHuy);
            dataItems.add(data1);
            dataItems.add(data2);
            dataItems.add(data3);
            dataItems.add(data4);
            dataItems.add(data5);
            dataItems.add(data6);
            dataItems.add(data7);
            dataItems.add(data8);
            dataItems.add(data9);
        }

        if (hoaDonHuyList.size() > 0){
            return new ServiceResult<>(AppConstant.SUCCESS,"get success",dataItems);
        }else{
            return new ServiceResult<>(AppConstant.NOT_FOUND,"khong co hoa don",null);
        }


    }

    @Override
    public ServiceResult<List<DataItem>> thongKeSanPhamBanChayTrongNam(Integer nam) {
        List<Object[]> getHangHoa = statisticalRepository.findByHangHoaBanChayTrongNam(nam);
        List<SoHangHoaTrongThang> result = new ArrayList<>();
        List<DataItem> dataItems = new ArrayList<>();

        for (Object[] record: getHangHoa){
            SoHangHoaTrongThang soHangHoaTrongThang = new SoHangHoaTrongThang();

            DataItem data1 = new DataItem();
            soHangHoaTrongThang.setValue1("thang1") ;
            data1.setType(soHangHoaTrongThang.getValue1());
            soHangHoaTrongThang.setThang1((BigDecimal) record[0]);
            data1.setValues(soHangHoaTrongThang.getThang1());

            DataItem data2 = new DataItem();
            soHangHoaTrongThang.setValue2("thang2");
            data2.setType(soHangHoaTrongThang.getValue2());
            soHangHoaTrongThang.setThang2((BigDecimal) record[1]);
            data2.setValues(soHangHoaTrongThang.getThang2());

            DataItem data3 = new DataItem();
            soHangHoaTrongThang.setValue3("thang3");
            data3.setType(soHangHoaTrongThang.getValue3());
            soHangHoaTrongThang.setThang3((BigDecimal) record[2]);
            data3.setValues(soHangHoaTrongThang.getThang3());

            DataItem data4 = new DataItem();
            soHangHoaTrongThang.setValue4("thang4");
            data4.setType(soHangHoaTrongThang.getValue4());
            soHangHoaTrongThang.setThang4((BigDecimal) record[3]);
            data4.setValues(soHangHoaTrongThang.getThang4());

            DataItem data5 = new DataItem();
            soHangHoaTrongThang.setValue5("thang5");
            data5.setType(soHangHoaTrongThang.getValue5());
            soHangHoaTrongThang.setThang5((BigDecimal) record[4]);
            data5.setValues(soHangHoaTrongThang.getThang5());

            DataItem data6 = new DataItem();
            soHangHoaTrongThang.setValue6("thang6");
            data6.setType(soHangHoaTrongThang.getValue6());
            soHangHoaTrongThang.setThang6((BigDecimal) record[5]);
            data6.setValues(soHangHoaTrongThang.getThang6());

            DataItem data7 = new DataItem();
            soHangHoaTrongThang.setValue7("thang7");
            data7.setType(soHangHoaTrongThang.getValue7());
            soHangHoaTrongThang.setThang7((BigDecimal) record[6]);
            data7.setValues(soHangHoaTrongThang.getThang7());

            DataItem data8 = new DataItem();
            soHangHoaTrongThang.setValue8("thang8");
            data8.setType(soHangHoaTrongThang.getValue8());
            soHangHoaTrongThang.setThang8((BigDecimal) record[7]);
            data8.setValues(soHangHoaTrongThang.getThang8());

            DataItem data9 = new DataItem();
            soHangHoaTrongThang.setValue9("thang9");
            data9.setType(soHangHoaTrongThang.getValue9());
            soHangHoaTrongThang.setThang9((BigDecimal) record[8]);
            data9.setValues(soHangHoaTrongThang.getThang9());

            DataItem data10 = new DataItem();
            soHangHoaTrongThang.setValue10("thang10");
            data10.setType(soHangHoaTrongThang.getValue10());
            soHangHoaTrongThang.setThang10((BigDecimal) record[9]);
            data10.setValues(soHangHoaTrongThang.getThang10());

            DataItem data11 = new DataItem();
            soHangHoaTrongThang.setValue11("thang11");
            data11.setType(soHangHoaTrongThang.getValue11());
            soHangHoaTrongThang.setThang11((BigDecimal) record[10]);
            data11.setValues(soHangHoaTrongThang.getThang11());

            DataItem data12 = new DataItem();
            soHangHoaTrongThang.setValue12("thang12");
            data12.setType(soHangHoaTrongThang.getValue12());
            soHangHoaTrongThang.setThang12((BigDecimal) record[11]);
            data12.setValues(soHangHoaTrongThang.getThang12());

            result.add(soHangHoaTrongThang);
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
    public ServiceResult<List<Top5SanPhamBanChayTrongThangVaNam>> sanPhamBanChay(Integer thang, Integer nam) {
        List<Object[]> topSanPhamBanChay = statisticalRepository.sanPhamBanChayTheoThangNam(thang, nam);
        List<Top5SanPhamBanChayTrongThangVaNam> result = new ArrayList<>();

        for (Object[] record: topSanPhamBanChay){
            Top5SanPhamBanChayTrongThangVaNam top5SanPhamBanChayTrongThangVaNam = new Top5SanPhamBanChayTrongThangVaNam();
            top5SanPhamBanChayTrongThangVaNam.setTenSanPham( (String) record[0]);
            top5SanPhamBanChayTrongThangVaNam.setSoLuong((BigInteger) record[1]);
            result.add(top5SanPhamBanChayTrongThangVaNam);

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
