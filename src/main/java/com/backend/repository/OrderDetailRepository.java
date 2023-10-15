package com.backend.repository;

import com.backend.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    @Query(value = "SELECT s.* FROM order_detail s where s.order_id = :idOrder", nativeQuery = true)
    Page<OrderDetail> orderDetailByOrderId (Pageable pageable, @Param("idOrder") Long idOrder);
}
