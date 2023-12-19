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

    @Query(value = "SELECT s.name, COUNT(s.name) as soLuong\n" +
            "FROM db_datn.my_order mo\n" +
            "JOIN order_detail od ON mo.id = od.order_id\n" +
            "JOIN shoe_detail sd ON sd.id = od.shoe_detail_id\n" +
            "JOIN shoe s ON s.id = sd.shoe_id\n" +
            "GROUP BY s.name\n" +
            "ORDER BY soLuong DESC\n" +
            "LIMIT 5;",nativeQuery = true)
    List<Object[]> sanPhamBanChayTheoThangNam();

    // thống kê doanh thu theo năm
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
    List<Object[]> thongKeDoanhThuTheoNam(Date ngayBatDau,Date ngayKetThuc, Integer typeBanHang);

    // doanh thu và số lượng hàng hóa theo ngày
    @Query(value = "SELECT \n" +
            "\t\tCASE WHEN date(mo.pay_date) = CURDATE() THEN sum(od.quantity * od.price) ELSE 0 END AS 'tongTien' ,\n" +
            "\t\tCASE WHEN date(mo.pay_date) = CURDATE() THEN sum(od.quantity) ELSE 0 END AS 'SoLuong'\n" +
            "\t\tfrom db_datn.my_order mo \n" +
            "\t\tjoin order_detail od on mo.id = od.order_id\n" +
            "        where date(mo.pay_date) = CURDATE() and mo.status = 8 and (:typeBanHang is null or mo.type = :typeBanHang)",nativeQuery = true)
    List<Object[]> doanhThuTheoNgay(Integer typeBanHang);

    // doanh thu và số lượng Hóa đơn theo ngày
    @Query(value = "select  CASE WHEN date(mo.pay_date) = CURDATE() THEN sum(mo.total_money) ELSE 0 END AS 'tongTien' ,\n" +
            "            CASE WHEN date(mo.pay_date) = CURDATE() THEN count(mo.status) ELSE 0 END AS 'SoLuongHoaDon'\n" +
            "            from db_datn.my_order mo\n" +
            "            where date(mo.pay_date) = CURDATE() and mo.status = 8 and (:typeBanHang is null or mo.type = :typeBanHang)",nativeQuery = true)
    List<Object[]> soHoaDonTrongNgay(Integer typeBanHang);

    // doanh thu và số lượng Hóa đơn theo tháng
    @Query(value = "select  CASE WHEN month(mo.pay_date) = MONTH(CURDATE()) THEN sum(mo.total_money) ELSE 0 END AS 'tongTien' ,\n" +
            "            CASE WHEN month(mo.pay_date) = MONTH(CURDATE()) THEN count(mo.status) ELSE 0 END AS 'SoLuongHoaDon'\n" +
            "            from db_datn.my_order mo\n" +
            "            where month(mo.pay_date) = MONTH(CURDATE()) and mo.status = 8 and (:typeBanHang is null or mo.type = :typeBanHang)",nativeQuery = true)
    List<Object[]> soHoaDonTheoThang(Integer typeBanHang);

    // doanh thu và số lượng Hóa đơn theo năm
    @Query(value = "select  CASE WHEN year(mo.pay_date) = year(CURDATE()) THEN sum(mo.total_money) ELSE 0 END AS 'tongTien' ,\n" +
            "                        CASE WHEN year(mo.pay_date) = year(CURDATE()) THEN count(mo.status) ELSE 0 END AS 'SoLuongHoaDon'\n" +
            "                        from db_datn.my_order mo\n" +
            "                        where year(mo.pay_date) = year(CURDATE()) and mo.status = 8 and (:typeBanHang is null or mo.type = :typeBanHang)",nativeQuery = true)
    List<Object[]> soHoaDonTheoNam(Integer typeBanHang);

    // doanh thu theo tháng, chọn năm hiện ra 12 tháng
    @Query(value = "SELECT \n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 1 THEN od.quantity * od.price ELSE 0 END), 0) AS 'totalThang1',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 1 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang1',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 2 THEN od.quantity * od.price ELSE 0 END), 0) AS 'totalThang2',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 2 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang2',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 3 THEN od.quantity * od.price ELSE 0 END), 0) AS 'totalThang3',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 3 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang3',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 4 THEN od.quantity * od.price ELSE 0 END), 0) AS 'totalThang4',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 4 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang4',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 5 THEN od.quantity * od.price ELSE 0 END), 0) AS 'totalThang5',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 5 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang5',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 6 THEN od.quantity * od.price ELSE 0 END), 0) AS 'totalThang6',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 6 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang6',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 7 THEN od.quantity * od.price ELSE 0 END), 0) AS 'totalThang7',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 7 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang7',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 8 THEN od.quantity * od.price ELSE 0 END), 0) AS 'totalThang8',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 8 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang8',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 9 THEN od.quantity * od.price ELSE 0 END), 0) AS 'totalThang9',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 9 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang9',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 10 THEN od.quantity * od.price ELSE 0 END), 0) AS 'totalThang10',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 10 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang10',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 11 THEN od.quantity * od.price ELSE 0 END), 0) AS 'totalThang11',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 11 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang11',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 12 THEN od.quantity * od.price ELSE 0 END), 0) AS 'totalThang12',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 12 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang12'\n" +
            "FROM my_order mo \n" +
            "JOIN payment_method pm ON mo.id = pm.order_id\n" +
            "JOIN order_detail od ON mo.id = od.order_id\n" +
            "WHERE\n" +
            "    YEAR(mo.pay_date) = :nam AND mo.status = 8 AND (:typeBanHang is null or mo.type = :typeBanHang)",nativeQuery = true)
    List<Object[]> doanhThuTheoThang(Integer nam, Integer typeBanHang);

    // số lượng sản phẩm bán được trong năm
    @Query(value = "SELECT \n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 1 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang1',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 2 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang2',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 3 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang3',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 4 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang4',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 5 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang5',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 6 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang6',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 7 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang7',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 8 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang8',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 9 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang9',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 10 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang10',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 11 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang11',\n" +
            "    COALESCE(SUM(CASE WHEN MONTH(mo.pay_date) = 12 THEN od.quantity ELSE 0 END), 0) AS 'soLuongThang12'\n" +
            "FROM my_order mo \n" +
            "JOIN payment_method pm ON mo.id = pm.order_id\n" +
            "JOIN order_detail od ON mo.id = od.order_id\n" +
            "WHERE\n" +
            "    YEAR(mo.pay_date) = :nam AND mo.status = 8;",nativeQuery = true)
    List<Object[]> soLuongSanPhamBanTheoNam(Integer nam);
}
