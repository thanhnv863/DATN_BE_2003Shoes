package com.backend.repository;

import com.backend.dto.response.OrderHistoryReponse;
import com.backend.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

    @Query(value = "SELECT s.* FROM order_history as s " +
            " where s.order_id = :idOrder order by s.created_time desc", nativeQuery = true)
    List<OrderHistory> getListOrderHistoryByOrder(@Param("idOrder") Long idOrder);
}
