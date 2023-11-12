package com.backend.repository;

import com.backend.entity.Address;
import com.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticalRepository extends JpaRepository<Order,Long> {

//    @Query("select a from Address a where " +
//            " a.name like concat('%', :name, '%')")
//    List<Address> searchNameClient(String name);

    @Query(value = "select \n" +
            "\tday(mo.create_date) as Ngay,\n" +
            "    COUNT(*) as soHoaDonHuy\n" +
            " from my_order mo \n" +
            "\t\twhere day(mo.create_date) = :ngayTao and status = :trangThai\n" +
            "        GROUP BY day(mo.create_date) \n" +
            "        ORDER BY Ngay",nativeQuery = true)
    List<Object[]> findByHoaDonHuy(Integer ngayTao, Integer trangThai);

    @Query(value = "select \n" +
            "    CASE WHEN month(oh.created_time) = 1 THEN sum(od.quantity) ELSE 0 END AS 'tháng 1',\n" +
            "    CASE WHEN month(oh.created_time) = 2 THEN sum(od.quantity) ELSE 0 END AS 'tháng 2',\n" +
            "    CASE WHEN month(oh.created_time) = 3 THEN sum(od.quantity) ELSE 0 END AS 'tháng 3',\n" +
            "    CASE WHEN month(oh.created_time) = 4 THEN sum(od.quantity) ELSE 0 END AS 'tháng 4',\n" +
            "    CASE WHEN month(oh.created_time) = 5 THEN sum(od.quantity) ELSE 0 END AS 'tháng 5',\n" +
            "    CASE WHEN month(oh.created_time) = 6 THEN sum(od.quantity) ELSE 0 END AS 'tháng 6',\n" +
            "    CASE WHEN month(oh.created_time) = 7 THEN sum(od.quantity) ELSE 0 END AS 'tháng 7',\n" +
            "    CASE WHEN month(oh.created_time) = 8 THEN sum(od.quantity) ELSE 0 END AS 'tháng 8',\n" +
            "    CASE WHEN month(oh.created_time) = 9 THEN sum(od.quantity) ELSE 0 END AS 'tháng 9',\n" +
            "    CASE WHEN month(oh.created_time) = 10 THEN sum(od.quantity) ELSE 0 END AS 'tháng 10',\n" +
            "    CASE WHEN month(oh.created_time) = 11 THEN sum(od.quantity) ELSE 0 END AS 'tháng 11',\n" +
            "    CASE WHEN month(oh.created_time) = 12 THEN sum(od.quantity) ELSE 0 END AS 'tháng 12',\n" +
            "\tcoalesce(sum(od.quantity), 0) as 'Tổng số lượng'\n" +
            " from order_detail od \n" +
            "\t\t\t join shoe_detail sd on od.shoe_detail_id = sd.id\n" +
            "             join order_history oh on od.order_id = oh.order_id\n" +
            "             join shoe s on sd.shoe_id = s.id\n" +
            "            where YEAR(oh.created_time) = :nam\t\n" +
            "            group by month(oh.created_time)\n",nativeQuery = true)
    List<Object[]> findByHangHoaBanChayTrongNam(Integer nam);

    @Query(value = "select \n" +
            "\tday(mo.create_date) as Ngay,\n" +
            "    sum(mo.total_money) as tongTien\n" +
            " from my_order mo \n" +
            "\t\twhere day(mo.create_date) = :ngay\n" +
            "        GROUP BY day(mo.create_date)\n" +
            "        ORDER BY Ngay",nativeQuery = true)
    List<Object[]> doanhThuTrongNgay(Integer ngay);

    @Query(value = "select \n" +
            "\tmonth(mo.create_date) as Thang,\n" +
            "    sum(mo.total_money) as tongTien\n" +
            " from my_order mo \n" +
            "\t\twhere month(mo.create_date) = :thang\n" +
            "        GROUP BY month(mo.create_date)\n" +
            "        ORDER BY Thang", nativeQuery = true)
    List<Object[]> doanhThuTrongThang(Integer thang);

}
