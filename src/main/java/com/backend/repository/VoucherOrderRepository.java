package com.backend.repository;

import com.backend.dto.response.VoucherOrderResponse;
import com.backend.entity.VoucherOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;

@Repository
public interface VoucherOrderRepository extends JpaRepository<VoucherOrder, Long> {
    @Query(value = "SELECT v.id,v.code,v.name, v.quantity,v.min_order_value,v.discount_amount,v.start_date,v.end_date,v.created_time,v.updated_time,v.reduce_form,v.status " +
            "FROM db_datn.voucher v \n" +
            "WHERE v.status in(0,1,2)", nativeQuery = true)
    Page<VoucherOrder> getAllVoucherOrder(Pageable pageable);

    @Query(value = "SELECT v.id,v.code,v.name, v.quantity,v.min_order_value,v.discount_amount,v.start_date,v.end_date,v.created_time,v.updated_time,v.reduce_form,v.status " +
            "FROM db_datn.voucher v \n" +
            "WHERE v.status in(0)", nativeQuery = true)
    Page<VoucherOrder> getAllVoucherStatus0(Pageable pageable);

    @Query(value = "SELECT v.id,v.code,v.name, v.quantity,v.min_order_value,v.discount_amount,v.start_date,v.end_date,v.created_time,v.updated_time,v.reduce_form,v.status " +
            "FROM db_datn.voucher v \n" +
            "WHERE v.status in(1)", nativeQuery = true)
    Page<VoucherOrder> getAllVoucherStatus1(Pageable pageable);

    @Query(value = "SELECT v.id,v.code,v.name, v.quantity,v.min_order_value,v.discount_amount,v.start_date,v.end_date,v.created_time,v.updated_time,v.reduce_form,v.status " +
            "FROM db_datn.voucher v \n" +
            "WHERE v.status in(2)", nativeQuery = true)
    Page<VoucherOrder> getAllVoucherStatus2(Pageable pageable);
}
