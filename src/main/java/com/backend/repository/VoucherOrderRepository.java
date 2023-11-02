package com.backend.repository;

import com.backend.dto.response.VoucherOrderResponse;
import com.backend.entity.Order;
import com.backend.entity.VoucherOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherOrderRepository extends JpaRepository<VoucherOrder, Long> {

    Optional<VoucherOrder> findVoucherByCode(String code);

    @Query(value = "SELECT * FROM voucher where voucher.code = :code", nativeQuery = true)
    VoucherOrder checkDuplicate(@Param("code") String code);

    @Query(value = "SELECT distinct reduce_form FROM voucher", nativeQuery = true)
    List<Integer> listAllByReduce();
}
