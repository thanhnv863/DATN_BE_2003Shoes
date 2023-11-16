package com.backend.repository;

import com.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StatisticalRepository extends JpaRepository<Order,Long> {

    @Query(value = "select sum(mo.status = 0) as hoaDonCho,\n" +
            "\t\tsum(mo.status = 1) as choThanhToan,\n" +
            "        sum(mo.status = 2) as daThanhToan,\n" +
            "        sum(mo.status = 3) as dahuy,\n" +
            "        sum(mo.status = 4) as choXacNhan,\n" +
            "        sum(mo.status = 5) as daXacNhan,\n" +
            "        sum(mo.status = 6) as choGiaoHang,\n" +
            "        sum(mo.status = 7) as daBanGiao,\n" +
            "        sum(mo.status = 8) as hoanThanh\n" +
            "\t\tfrom my_order mo \n" +
            "\t\twhere mo.create_date between :ngayBatDau and :ngayKetThuc",nativeQuery = true)
    List<Object[]> findByHoaDon(Date ngayBatDau, Date ngayKetThuc);

    @Query(value = "select \n" +
            "    CASE WHEN month(mo.pay_date) = 1 THEN sum(od.quantity) ELSE 0 END AS 'tháng 1',\n" +
            "    CASE WHEN month(mo.pay_date) = 2 THEN sum(od.quantity) ELSE 0 END AS 'tháng 2',\n" +
            "    CASE WHEN month(mo.pay_date) = 3 THEN sum(od.quantity) ELSE 0 END AS 'tháng 3',\n" +
            "    CASE WHEN month(mo.pay_date) = 4 THEN sum(od.quantity) ELSE 0 END AS 'tháng 4',\n" +
            "    CASE WHEN month(mo.pay_date) = 5 THEN sum(od.quantity) ELSE 0 END AS 'tháng 5',\n" +
            "    CASE WHEN month(mo.pay_date) = 6 THEN sum(od.quantity) ELSE 0 END AS 'tháng 6',\n" +
            "    CASE WHEN month(mo.pay_date) = 7 THEN sum(od.quantity) ELSE 0 END AS 'tháng 7',\n" +
            "    CASE WHEN month(mo.pay_date) = 8 THEN sum(od.quantity) ELSE 0 END AS 'tháng 8',\n" +
            "    CASE WHEN month(mo.pay_date) = 9 THEN sum(od.quantity) ELSE 0 END AS 'tháng 9',\n" +
            "    CASE WHEN month(mo.pay_date) = 10 THEN sum(od.quantity) ELSE 0 END AS 'tháng 10',\n" +
            "    CASE WHEN month(mo.pay_date) = 11 THEN sum(od.quantity) ELSE 0 END AS 'tháng 11',\n" +
            "    CASE WHEN month(mo.pay_date) = 12 THEN sum(od.quantity) ELSE 0 END AS 'tháng 12',\n" +
            "\tcoalesce(sum(od.quantity), 0) as 'Tổng số lượng'\n" +
            " from order_detail od \n" +
            "\t\t\tleft join my_order mo on mo.id = od.order_id\n" +
            "            where YEAR(mo.pay_date) = :nam and od.status=1\n" +
            "            group by month(mo.pay_date)",nativeQuery = true)
    List<Object[]> findByHangHoaBanChayTrongNam(Integer nam);

    @Query(value = "SELECT s.name ,count(s.name) as soLuong\n" +
            " FROM db_datn.my_order mo\n" +
            "\tleft join order_detail od on mo.id = od.order_id\n" +
            "    left join shoe_detail sd on sd.id = od.shoe_detail_id\n" +
            "    left join shoe s on s.id = sd.shoe_id\n" +
            "    where mo.pay_date between :ngayBatDau and :ngayKetThuc\n" +
            "    group by s.name\n" +
            "    order by soLuong desc\n" +
            "    limit 5;",nativeQuery = true)
    List<Object[]> sanPhamBanChayTheoThangNam(Date ngayBatDau,Date ngayKetThuc);

    @Query(value = "SELECT \n" +
            "\tYEAR(mo.pay_date) - 1 AS ResultString1,\n" +
            "\tCASE WHEN year(mo.pay_date) = year(mo.pay_date) - 1 THEN sum(mo.total_money) ELSE 0 END AS 'nam 2022',\n" +
            "    CASE WHEN year(mo.pay_date) = year(mo.pay_date) - 1 THEN sum(od.quantity) ELSE 0 END AS 'soLuong1',\n" +
            "    YEAR(mo.pay_date) AS ResultString2,\n" +
            "    CASE WHEN year(mo.pay_date) = year(mo.pay_date)  THEN sum(mo.total_money) ELSE 0 END AS 'nam 2023',\n" +
            "    CASE WHEN year(mo.pay_date) = year(mo.pay_date) THEN sum(od.quantity) ELSE 0 END AS 'soLuong2',\n" +
            "\tYEAR(mo.pay_date) + 1 AS ResultString3,\n" +
            "    CASE WHEN year(mo.pay_date) = year(mo.pay_date) + 1  THEN sum(mo.total_money) ELSE 0 END AS 'nam 2024',\n" +
            "    CASE WHEN year(mo.pay_date) = year(mo.pay_date) + 1 THEN sum(od.quantity) ELSE 0 END AS 'soLuong3',\n" +
            "    YEAR(mo.pay_date) + 2 AS ResultString4,\n" +
            "    CASE WHEN year(mo.pay_date) = year(mo.pay_date) + 2  THEN sum(mo.total_money) ELSE 0 END AS 'nam 2025',\n" +
            "    CASE WHEN year(mo.pay_date) = year(mo.pay_date) + 2 THEN sum(od.quantity) ELSE 0 END AS 'soLuong4',\n" +
            "    YEAR(mo.pay_date) + 3 AS ResultString5,\n" +
            "    CASE WHEN year(mo.pay_date) = year(mo.pay_date) + 3  THEN sum(mo.total_money) ELSE 0 END AS 'nam 2026',\n" +
            "    CASE WHEN year(mo.pay_date) = year(mo.pay_date) + 3 THEN sum(od.quantity) ELSE 0 END AS 'soLuong5'\n" +
            "\tfrom db_datn.my_order mo \n" +
            "    left join order_detail od on mo.id = od.order_id\n" +
            "    where mo.pay_date between :ngayBatDau and :ngayKetThuc \n" +
            "\t\tand mo.status = 2 and mo.type = :typeBanHang and od.status = 1\n" +
            "    group by mo.status and mo.type and od.quantity",nativeQuery = true)
    List<Object[]> thongKeDoanhThu(Date ngayBatDau,Date ngayKetThuc, Integer typeBanHang);

    @Query(value = "SELECT \n" +
            "\t sum(mo.total_money) as tongTien\n" +
            "\tfrom db_datn.my_order mo \n" +
            "    where mo.pay_date between :ngayBatDau and :ngayKetThuc and mo.status = 8\n" +
            "\t\tand mo.type = :typeBanHang",nativeQuery = true)
    List<Object[]> doanhThuTheoNgay(Date ngayBatDau,Date ngayKetThuc, Integer typeBanHang);
}
