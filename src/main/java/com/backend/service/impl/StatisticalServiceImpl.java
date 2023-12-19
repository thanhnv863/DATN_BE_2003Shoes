package com.backend.service.impl;

import com.backend.ServiceResult;
import com.backend.config.AppConstant;
import com.backend.dto.statistical.DataItem;
import com.backend.dto.statistical.DataItemDoanhThu;
import com.backend.dto.statistical.DataItemDoanhThuThang;
import com.backend.dto.statistical.DoanhThuTheoNgay;
import com.backend.dto.statistical.DoanhThuTheoThang;
import com.backend.dto.statistical.HoaDonHuy;
import com.backend.dto.statistical.SoHangHoaBanDuocTrongNam;
import com.backend.dto.statistical.SoHangHoaTrongThang;
import com.backend.dto.statistical.SoHoaDon;
import com.backend.dto.statistical.ThongKeDoanhThu;
import com.backend.dto.statistical.Top5SanPhamBanChayTrongThangVaNam;
import com.backend.repository.StatisticalRepository;
import com.backend.service.IStatistical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StatisticalServiceImpl implements IStatistical {

    @Autowired
    private StatisticalRepository statisticalRepository;

    @Autowired
    private EntityManager entityManager;

    @Override
    public ServiceResult<List<DataItem>> findHoaDon(Date ngayBatDau, Date ngayKetThuc) {
        List<Object[]> soHoaDonHuy = statisticalRepository.findByHoaDon(ngayBatDau, ngayKetThuc);
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
    public ServiceResult<List<DataItemDoanhThu>> thongKeDoanhThu(Date ngayBatDau, Date ngayKetThuc, Integer typeBanHang) {
        StringBuilder sql = new StringBuilder();
        sql.append("select \n" +
                "\t\t\t    CASE WHEN month(mo.pay_date) = 1 THEN sum(od.quantity * od.price) ELSE 0 END AS 'totalThang1',\n" +
                "                CASE WHEN month(mo.pay_date) = 1 THEN sum(od.quantity) ELSE 0 END AS 'soLuongThang1',\n" +
                "                CASE WHEN month(mo.pay_date) = 2 THEN sum(od.quantity * od.price) ELSE 0 END AS 'totalThang2',\n" +
                "                CASE WHEN month(mo.pay_date) = 2 THEN sum(od.quantity) ELSE 0 END AS 'soLuongThang2',\n" +
                "                CASE WHEN month(mo.pay_date) = 3 THEN sum(od.quantity * od.price) ELSE 0 END AS 'totalThang3',\n" +
                "                CASE WHEN month(mo.pay_date) = 3 THEN sum(od.quantity) ELSE 0 END AS 'soLuongThang3',\n" +
                "                CASE WHEN month(mo.pay_date) = 4 THEN sum(od.quantity * od.price) ELSE 0 END AS 'totalThang4',\n" +
                "                CASE WHEN month(mo.pay_date) = 4 THEN sum(od.quantity) ELSE 0 END AS 'soLuongThang4',\n" +
                "                CASE WHEN month(mo.pay_date) = 5 THEN sum(od.quantity * od.price) ELSE 0 END AS 'totalThang5',\n" +
                "                CASE WHEN month(mo.pay_date) = 5 THEN sum(od.quantity) ELSE 0 END AS 'soLuongThang5',\n" +
                "                CASE WHEN month(mo.pay_date) = 6 THEN sum(od.quantity * od.price) ELSE 0 END AS 'totalThang6',\n" +
                "                CASE WHEN month(mo.pay_date) = 6 THEN sum(od.quantity) ELSE 0 END AS 'soLuongThang6',\n" +
                "                CASE WHEN month(mo.pay_date) = 7 THEN sum(od.quantity * od.price) ELSE 0 END AS 'totalThang7',\n" +
                "                CASE WHEN month(mo.pay_date) = 7 THEN sum(od.quantity) ELSE 0 END AS 'soLuongThang7',\n" +
                "                CASE WHEN month(mo.pay_date) = 8 THEN sum(od.quantity * od.price) ELSE 0 END AS 'totalThang8',\n" +
                "                CASE WHEN month(mo.pay_date) = 8 THEN sum(od.quantity) ELSE 0 END AS 'soLuongThang8',\n" +
                "                CASE WHEN month(mo.pay_date) = 9 THEN sum(od.quantity * od.price) ELSE 0 END AS 'totalThang9',\n" +
                "                CASE WHEN month(mo.pay_date) = 9 THEN sum(od.quantity) ELSE 0 END AS 'soLuongThang9',\n" +
                "                CASE WHEN month(mo.pay_date) = 10 THEN sum(od.quantity * od.price) ELSE 0 END AS 'totalThang10',\n" +
                "                CASE WHEN month(mo.pay_date) = 10 THEN sum(od.quantity) ELSE 0 END AS 'soLuongThang10',\n" +
                "                CASE WHEN month(mo.pay_date) = 11 THEN sum(od.quantity * od.price) ELSE 0 END AS 'totalThang11',\n" +
                "                CASE WHEN month(mo.pay_date) = 11 THEN sum(od.quantity) ELSE 0 END AS 'soLuong11',\n" +
                "                CASE WHEN month(mo.pay_date) = 12 THEN sum(od.quantity * od.price) ELSE 0 END AS 'totalThang12',\n" +
                "                CASE WHEN month(mo.pay_date) = 12 THEN sum(od.quantity) ELSE 0 END AS 'soLuongThang12'");
        sql.append("from my_order mo");
        sql.append("left join payment_method pm on mo.id = pm.order_id");
        sql.append("left join order_detail od on mo.id = od.order_id");
        sql.append("where YEAR(mo.pay_date) = :nam and mo.status = 2 and (:typeBanHang is null or mo.type = :typeBanHang)");
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("typeBanHang", typeBanHang);

        List<Object[]> getThongKe = statisticalRepository.thongKeDoanhThuTheoNam(ngayBatDau, ngayKetThuc, typeBanHang);
        List<ThongKeDoanhThu> result = new ArrayList<>();
        List<DataItemDoanhThu> dataItems = new ArrayList<>();


        for (Object[] records: getThongKe){
            ThongKeDoanhThu thongKeDoanhThu = new ThongKeDoanhThu();

            DataItemDoanhThu dataItemDoanhThu1 = new DataItemDoanhThu();
            thongKeDoanhThu.setNam1((Integer) records[0]);
            dataItemDoanhThu1.setYear(thongKeDoanhThu.getNam1());
            thongKeDoanhThu.setTongTien1((BigDecimal) records[1]);
            dataItemDoanhThu1.setValue(thongKeDoanhThu.getTongTien1());
            thongKeDoanhThu.setSoLuong1((BigDecimal) records[2]);
            dataItemDoanhThu1.setCount(thongKeDoanhThu.getSoLuong1());

            DataItemDoanhThu dataItemDoanhThu2 = new DataItemDoanhThu();
            thongKeDoanhThu.setNam2((Integer) records[3]);
            dataItemDoanhThu2.setYear(thongKeDoanhThu.getNam2());
            thongKeDoanhThu.setTongTien2((BigDecimal) records[4]);
            dataItemDoanhThu2.setValue(thongKeDoanhThu.getTongTien2());
            thongKeDoanhThu.setSoLuong2((BigDecimal) records[5]);
            dataItemDoanhThu2.setCount(thongKeDoanhThu.getSoLuong2());

            DataItemDoanhThu dataItemDoanhThu3 = new DataItemDoanhThu();
            thongKeDoanhThu.setNam3((Integer) records[6]);
            dataItemDoanhThu3.setYear(thongKeDoanhThu.getNam3());
            thongKeDoanhThu.setTongTien3((BigDecimal) records[7]);
            dataItemDoanhThu3.setValue(thongKeDoanhThu.getTongTien3());
            thongKeDoanhThu.setSoLuong3((BigDecimal) records[8]);
            dataItemDoanhThu3.setCount(thongKeDoanhThu.getSoLuong3());

            DataItemDoanhThu dataItemDoanhThu4 = new DataItemDoanhThu();
            thongKeDoanhThu.setNam4((Integer) records[9]);
            dataItemDoanhThu4.setYear(thongKeDoanhThu.getNam4());
            thongKeDoanhThu.setTongTien4((BigDecimal) records[10]);
            dataItemDoanhThu4.setValue(thongKeDoanhThu.getTongTien4());
            thongKeDoanhThu.setSoLuong4((BigDecimal) records[11]);
            dataItemDoanhThu4.setCount(thongKeDoanhThu.getSoLuong4());

            DataItemDoanhThu dataItemDoanhThu5 = new DataItemDoanhThu();
            thongKeDoanhThu.setNam5((Integer) records[12]);
            dataItemDoanhThu5.setYear(thongKeDoanhThu.getNam5());
            thongKeDoanhThu.setTongTien5((BigDecimal) records[13]);
            dataItemDoanhThu5.setValue(thongKeDoanhThu.getTongTien5());
            thongKeDoanhThu.setSoLuong5((BigDecimal) records[14]);
            dataItemDoanhThu5.setCount(thongKeDoanhThu.getTongTien5());

            result.add(thongKeDoanhThu);
            dataItems.add(dataItemDoanhThu1);
            dataItems.add(dataItemDoanhThu2);
            dataItems.add(dataItemDoanhThu3);
            dataItems.add(dataItemDoanhThu4);
            dataItems.add(dataItemDoanhThu5);
        }

        if(result.size() > 0){
            return new ServiceResult<>(AppConstant.SUCCESS,"get success",dataItems);
        }else{
            return new ServiceResult<>(AppConstant.NOT_FOUND,"khong co san pham nao",null);
        }
    }

    @Override
    public ServiceResult<List<DataItemDoanhThuThang>> doanhThuTheoThang(Integer nam, Integer typeBanHang) {
        List<Object[]> listDoanhThu = statisticalRepository.doanhThuTheoThang(nam, typeBanHang);
        List<DoanhThuTheoThang> result = new ArrayList<>();
        List<DataItemDoanhThuThang> dataItems = new ArrayList<>();

        for (Object[] record: listDoanhThu){
            DoanhThuTheoThang doanhThuTheoThang = new DoanhThuTheoThang();

            DataItemDoanhThuThang dataItemDoanhThu1 = new DataItemDoanhThuThang();
            dataItemDoanhThu1.setTime("Thang1");
            doanhThuTheoThang.setTotalThang1((BigDecimal) record[0]);
            dataItemDoanhThu1.setTien(doanhThuTheoThang.getTotalThang1());
            doanhThuTheoThang.setSoLuongThang1((BigDecimal) record[1]);
            dataItemDoanhThu1.setSoLuong(doanhThuTheoThang.getSoLuongThang1());

            DataItemDoanhThuThang dataItemDoanhThu2 = new DataItemDoanhThuThang();
            dataItemDoanhThu2.setTime("Thang2");
            doanhThuTheoThang.setTotalThang2((BigDecimal) record[2]);
            dataItemDoanhThu2.setTien(doanhThuTheoThang.getTotalThang2());
            doanhThuTheoThang.setSoLuongThang2((BigDecimal) record[3]);
            dataItemDoanhThu2.setSoLuong(doanhThuTheoThang.getSoLuongThang2());

            DataItemDoanhThuThang dataItemDoanhThu3 = new DataItemDoanhThuThang();
            dataItemDoanhThu3.setTime("Thang3");
            doanhThuTheoThang.setTotalThang3((BigDecimal) record[4]);
            dataItemDoanhThu3.setTien(doanhThuTheoThang.getTotalThang3());
            doanhThuTheoThang.setSoLuongThang3((BigDecimal) record[5]);
            dataItemDoanhThu3.setSoLuong(doanhThuTheoThang.getSoLuongThang3());

            DataItemDoanhThuThang dataItemDoanhThu4 = new DataItemDoanhThuThang();
            dataItemDoanhThu4.setTime("Thang4");
            doanhThuTheoThang.setTotalThang4((BigDecimal) record[6]);
            dataItemDoanhThu4.setTien(doanhThuTheoThang.getTotalThang4());
            doanhThuTheoThang.setSoLuongThang4((BigDecimal) record[7]);
            dataItemDoanhThu4.setSoLuong(doanhThuTheoThang.getSoLuongThang4());

            DataItemDoanhThuThang dataItemDoanhThu5 = new DataItemDoanhThuThang();
            dataItemDoanhThu5.setTime("Thang5");
            doanhThuTheoThang.setTotalThang5((BigDecimal) record[8]);
            dataItemDoanhThu5.setTien(doanhThuTheoThang.getTotalThang5());
            doanhThuTheoThang.setSoLuongThang5((BigDecimal) record[9]);
            dataItemDoanhThu5.setSoLuong(doanhThuTheoThang.getSoLuongThang5());

            DataItemDoanhThuThang dataItemDoanhThu6 = new DataItemDoanhThuThang();
            dataItemDoanhThu6.setTime("Thang6");
            doanhThuTheoThang.setTotalThang6((BigDecimal) record[10]);
            dataItemDoanhThu6.setTien(doanhThuTheoThang.getTotalThang6());
            doanhThuTheoThang.setSoLuongThang6((BigDecimal) record[11]);
            dataItemDoanhThu6.setSoLuong(doanhThuTheoThang.getSoLuongThang6());

            DataItemDoanhThuThang dataItemDoanhThu7 = new DataItemDoanhThuThang();
            dataItemDoanhThu7.setTime("Thang7");
            doanhThuTheoThang.setTotalThang7((BigDecimal) record[12]);
            dataItemDoanhThu7.setTien(doanhThuTheoThang.getTotalThang7());
            doanhThuTheoThang.setSoLuongThang7((BigDecimal) record[13]);
            dataItemDoanhThu7.setSoLuong(doanhThuTheoThang.getSoLuongThang7());

            DataItemDoanhThuThang dataItemDoanhThu8 = new DataItemDoanhThuThang();
            dataItemDoanhThu8.setTime("Thang8");
            doanhThuTheoThang.setTotalThang8((BigDecimal) record[14]);
            dataItemDoanhThu8.setTien(doanhThuTheoThang.getTotalThang8());
            doanhThuTheoThang.setSoLuongThang8((BigDecimal) record[15]);
            dataItemDoanhThu8.setSoLuong(doanhThuTheoThang.getSoLuongThang8());

            DataItemDoanhThuThang dataItemDoanhThu9 = new DataItemDoanhThuThang();
            dataItemDoanhThu9.setTime("Thang9");
            doanhThuTheoThang.setTotalThang9((BigDecimal) record[16]);
            dataItemDoanhThu9.setTien(doanhThuTheoThang.getTotalThang9());
            doanhThuTheoThang.setSoLuongThang9((BigDecimal) record[17]);
            dataItemDoanhThu9.setSoLuong(doanhThuTheoThang.getSoLuongThang9());

            DataItemDoanhThuThang dataItemDoanhThu10 = new DataItemDoanhThuThang();
            dataItemDoanhThu10.setTime("Thang10");
            doanhThuTheoThang.setTotalThang10((BigDecimal) record[18]);
            dataItemDoanhThu10.setTien(doanhThuTheoThang.getTotalThang10());
            doanhThuTheoThang.setSoLuongThang10((BigDecimal) record[19]);
            dataItemDoanhThu10.setSoLuong(doanhThuTheoThang.getSoLuongThang10());

            DataItemDoanhThuThang dataItemDoanhThu11 = new DataItemDoanhThuThang();
            dataItemDoanhThu11.setTime("Thang11");
            doanhThuTheoThang.setTotalThang11((BigDecimal) record[20]);
            dataItemDoanhThu11.setTien(doanhThuTheoThang.getTotalThang11());
            doanhThuTheoThang.setSoLuongThang11((BigDecimal) record[21]);
            dataItemDoanhThu11.setSoLuong(doanhThuTheoThang.getSoLuongThang11());

            DataItemDoanhThuThang dataItemDoanhThu12 = new DataItemDoanhThuThang();
            dataItemDoanhThu12.setTime("Thang12");
            doanhThuTheoThang.setTotalThang12((BigDecimal) record[22]);
            dataItemDoanhThu12.setTien(doanhThuTheoThang.getTotalThang12());
            doanhThuTheoThang.setSoLuongThang12((BigDecimal) record[23]);
            dataItemDoanhThu12.setSoLuong(doanhThuTheoThang.getSoLuongThang12());

            result.add(doanhThuTheoThang);
            dataItems.add(dataItemDoanhThu1);
            dataItems.add(dataItemDoanhThu2);
            dataItems.add(dataItemDoanhThu3);
            dataItems.add(dataItemDoanhThu4);
            dataItems.add(dataItemDoanhThu5);
            dataItems.add(dataItemDoanhThu6);
            dataItems.add(dataItemDoanhThu7);
            dataItems.add(dataItemDoanhThu8);
            dataItems.add(dataItemDoanhThu9);
            dataItems.add(dataItemDoanhThu10);
            dataItems.add(dataItemDoanhThu11);
            dataItems.add(dataItemDoanhThu12);
        }

        if(result.size() > 0){
            return new ServiceResult<>(AppConstant.SUCCESS,"get success",dataItems);
        }else{
            return new ServiceResult<>(AppConstant.NOT_FOUND,"khong co san pham nao",null);
        }
    }

    @Override
    public ServiceResult<List<DoanhThuTheoNgay>> doanhThuTheoNgay(Integer typeBanHang) {
        List<Object[]> listDoanhThu = statisticalRepository.doanhThuTheoNgay(typeBanHang);
        List<DoanhThuTheoNgay> result = new ArrayList<>();

        for (Object[] record: listDoanhThu){
            DoanhThuTheoNgay doanhThuTheoNgay = new DoanhThuTheoNgay();
            doanhThuTheoNgay.setTongTien((BigDecimal) record[0]);
            doanhThuTheoNgay.setSoLuongHangHoa((BigDecimal) record[1]);

            result.add(doanhThuTheoNgay);
        }


        if(result.size() > 0){
            return new ServiceResult<>(AppConstant.SUCCESS,"get success",result);
        }else{
            return new ServiceResult<>(AppConstant.NOT_FOUND,"khong co san pham nao",null);
        }
    }

    @Override
    public ServiceResult<List<SoHoaDon>> soHoaDonBanTrongNgay(Integer typeBanHang) {
        List<Object[]> listHoaDon = statisticalRepository.soHoaDonTrongNgay(typeBanHang);
        List<SoHoaDon> result = new ArrayList<>();

        for (Object[] record: listHoaDon){
            SoHoaDon soHoaDon = new SoHoaDon();
            soHoaDon.setTongTien((BigDecimal) record[0]);
            soHoaDon.setSoHoaDon((BigInteger) record[1]);

            result.add(soHoaDon);
        }
        return new ServiceResult<>(AppConstant.SUCCESS,"get success",result);
    }

    @Override
    public ServiceResult<List<SoHoaDon>> soHoaDonBanTrongThang(Integer typeBanHang) {
        List<Object[]> listHoaDon = statisticalRepository.soHoaDonTheoThang(typeBanHang);
        List<SoHoaDon> result = new ArrayList<>();
        for (Object[] record: listHoaDon){
            SoHoaDon soHoaDon = new SoHoaDon();
            soHoaDon.setTongTien((BigDecimal) record[0]);
            soHoaDon.setSoHoaDon((BigInteger) record[1]);

            result.add(soHoaDon);
        }

        return new ServiceResult<>(AppConstant.SUCCESS,"get success",result);
    }

    @Override
    public ServiceResult<List<SoHoaDon>> soHoaDonBanTrongNam(Integer typeBanHang) {
        List<Object[]> listHoaDon = statisticalRepository.soHoaDonTheoNam(typeBanHang);
        List<SoHoaDon> result = new ArrayList<>();
        for (Object[] record: listHoaDon){
            SoHoaDon soHoaDon = new SoHoaDon();
            soHoaDon.setTongTien((BigDecimal) record[0]);
            soHoaDon.setSoHoaDon((BigInteger) record[1]);

            result.add(soHoaDon);
        }

        return new ServiceResult<>(AppConstant.SUCCESS,"get success",result);
    }


    @Override
    public ServiceResult<List<Top5SanPhamBanChayTrongThangVaNam>> sanPhamBanChay() {
        List<Object[]> topSanPhamBanChay = statisticalRepository.sanPhamBanChayTheoThangNam();
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
    public ServiceResult<List<DataItem>> soHangHoaBanDuocTrongNam(Integer nam) {
        List<Object[]> soHangHoa = statisticalRepository.soLuongSanPhamBanTheoNam(nam);
        List<SoHangHoaBanDuocTrongNam> result = new ArrayList<>();
        List<DataItem> dataItems = new ArrayList<>();


        for (Object[] record: soHangHoa) {
            SoHangHoaBanDuocTrongNam soHangHoaBanDuocTrongNam = new SoHangHoaBanDuocTrongNam();

            DataItem dataItem1 = new DataItem();
            soHangHoaBanDuocTrongNam.setValue1("Thang 1");
            dataItem1.setType(soHangHoaBanDuocTrongNam.getValue1());
            soHangHoaBanDuocTrongNam.setThang1((BigDecimal) record[0]);
            dataItem1.setValues(soHangHoaBanDuocTrongNam.getThang1());

            DataItem dataItem2 = new DataItem();
            soHangHoaBanDuocTrongNam.setValue2("Thang 2");
            dataItem2.setType(soHangHoaBanDuocTrongNam.getValue2());
            soHangHoaBanDuocTrongNam.setThang2((BigDecimal) record[1]);
            dataItem2.setValues(soHangHoaBanDuocTrongNam.getThang2());

            DataItem dataItem3 = new DataItem();
            soHangHoaBanDuocTrongNam.setValue3("Thang 3");
            dataItem3.setType(soHangHoaBanDuocTrongNam.getValue3());
            soHangHoaBanDuocTrongNam.setThang3((BigDecimal) record[2]);
            dataItem3.setValues(soHangHoaBanDuocTrongNam.getThang3());

            DataItem dataItem4 = new DataItem();
            soHangHoaBanDuocTrongNam.setValue4("Thang 4");
            dataItem4.setType(soHangHoaBanDuocTrongNam.getValue4());
            soHangHoaBanDuocTrongNam.setThang4((BigDecimal) record[3]);
            dataItem4.setValues(soHangHoaBanDuocTrongNam.getThang4());

            DataItem dataItem5 = new DataItem();
            soHangHoaBanDuocTrongNam.setValue5("Thang 5");
            dataItem5.setType(soHangHoaBanDuocTrongNam.getValue5());
            soHangHoaBanDuocTrongNam.setThang5((BigDecimal) record[4]);
            dataItem5.setValues(soHangHoaBanDuocTrongNam.getThang5());

            DataItem dataItem6 = new DataItem();
            soHangHoaBanDuocTrongNam.setValue6("Thang 6");
            dataItem6.setType(soHangHoaBanDuocTrongNam.getValue6());
            soHangHoaBanDuocTrongNam.setThang6((BigDecimal) record[5]);
            dataItem6.setValues(soHangHoaBanDuocTrongNam.getThang6());

            DataItem dataItem7 = new DataItem();
            soHangHoaBanDuocTrongNam.setValue7("Thang 7");
            dataItem7.setType(soHangHoaBanDuocTrongNam.getValue7());
            soHangHoaBanDuocTrongNam.setThang7((BigDecimal) record[6]);
            dataItem7.setValues(soHangHoaBanDuocTrongNam.getThang7());

            DataItem dataItem8 = new DataItem();
            soHangHoaBanDuocTrongNam.setValue8("Thang 8");
            dataItem8.setType(soHangHoaBanDuocTrongNam.getValue8());
            soHangHoaBanDuocTrongNam.setThang8((BigDecimal) record[7]);
            dataItem8.setValues(soHangHoaBanDuocTrongNam.getThang8());

            DataItem dataItem9 = new DataItem();
            soHangHoaBanDuocTrongNam.setValue9("Thang 9");
            dataItem9.setType(soHangHoaBanDuocTrongNam.getValue9());
            soHangHoaBanDuocTrongNam.setThang9((BigDecimal) record[8]);
            dataItem9.setValues(soHangHoaBanDuocTrongNam.getThang9());

            DataItem dataItem10 = new DataItem();
            soHangHoaBanDuocTrongNam.setValue10("Thang 10");
            dataItem10.setType(soHangHoaBanDuocTrongNam.getValue10());
            soHangHoaBanDuocTrongNam.setThang10((BigDecimal) record[9]);
            dataItem10.setValues(soHangHoaBanDuocTrongNam.getThang10());

            DataItem dataItem11 = new DataItem();
            soHangHoaBanDuocTrongNam.setValue11("Thang 11");
            dataItem11.setType(soHangHoaBanDuocTrongNam.getValue11());
            soHangHoaBanDuocTrongNam.setThang11((BigDecimal) record[10]);
            dataItem11.setValues(soHangHoaBanDuocTrongNam.getThang11());

            DataItem dataItem12 = new DataItem();
            soHangHoaBanDuocTrongNam.setValue12("Thang 12");
            dataItem12.setType(soHangHoaBanDuocTrongNam.getValue12());
            soHangHoaBanDuocTrongNam.setThang12((BigDecimal) record[11]);
            dataItem12.setValues(soHangHoaBanDuocTrongNam.getThang12());

            result.add(soHangHoaBanDuocTrongNam);
            dataItems.add(dataItem1);
            dataItems.add(dataItem2);
            dataItems.add(dataItem3);
            dataItems.add(dataItem4);
            dataItems.add(dataItem5);
            dataItems.add(dataItem6);
            dataItems.add(dataItem7);
            dataItems.add(dataItem8);
            dataItems.add(dataItem9);
            dataItems.add(dataItem10);
            dataItems.add(dataItem11);
            dataItems.add(dataItem12);
        }

        return new ServiceResult<>(AppConstant.SUCCESS,"success",dataItems);
    }
}
