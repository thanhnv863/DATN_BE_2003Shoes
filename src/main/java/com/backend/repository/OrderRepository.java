package com.backend.repository;

import com.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findOrderByCode(String code);



    @Query(value = "SELECT a.id, b.name, a.code, a.type, a.customer_name, a.phone_number" +
            ",a.address, a.ship_fee, a.money_reduce, a.total_money, a.create_date" +
            ",a.pay_date, a.ship_date, a.desired_date, a.receive_date, a.created_by" +
            ",a.updated_by,a.note, a.status FROM my_order as a " +
            " LEFT JOIN voucher b on a.voucher_id = b.id " +
            " LEFT JOIN account c on a.account_id = c.id " +
            " LEFT JOIN address d on c.id = d.account_id where a.status = :status", nativeQuery = true)
    List<Object> listOrderByStatus(@Param("status") Integer status);

    @Query(value = "SELECT * FROM my_order where my_order.account_id = ?1 order by my_order.create_date desc", nativeQuery = true)
    List<Order> listOrderCustomer(Long idAccount);

}
