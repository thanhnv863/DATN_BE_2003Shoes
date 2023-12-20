package com.backend.repository;

import com.backend.dto.response.OrderHistoryReponse;
import com.backend.dto.response.orderDetail.OrderDetailPDFResponse;
import com.backend.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

//    @Query(value = "SELECT a.code, s.created_time, s.created_by, s.note FROM order_history as s\n" +
//            " join my_order as a on s.order_id = a.id" +
//            " WHERE s.order_id = :idOrder\n" +
//            " GROUP BY s.order_id, a.code, s.created_time, s.created_by, s.note\n" +
//            " ORDER BY s.created_time;\n", nativeQuery = true)
//    List<OrderHistoryReponse> getListOrderHistoryByOrder(@Param("idOrder") Long idOrder);

    @Query(value = "SELECT NEW com.backend.dto.response.OrderHistoryReponse" +
            "(a.code, s.createdTime,s.createdBy,s.type,s.note)" +
            " FROM OrderHistory as s" +
            " join Order as a on s.order.id = a.id" +
            " where s.order.id = :idOrder GROUP BY a.code, s.createdTime,s.createdBy,s.type,s.note ORDER BY s.createdTime")
    List<OrderHistoryReponse> getListOrderHistoryByOrder(@Param("idOrder") Long idOrder);
}
