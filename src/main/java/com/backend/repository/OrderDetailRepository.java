package com.backend.repository;

import com.backend.dto.response.OrderDetailReponse;
import com.backend.dto.response.orderDetail.OrderDetailPDFResponse;
import com.backend.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {


    @Query(value = "SELECT NEW com.backend.dto.response.OrderDetailReponse" +
            "(s.id,s.shoeDetail.id,s.shoeDetail.code,c.imgUrl,s.order.code,s.quantity,s.price,s.discount,s.status)" +
            " FROM OrderDetail as s" +
            " left join ShoeDetail as b on s.shoeDetail.id = b.id" +
            " left join Thumbnail c on c.shoeDetail.id = b.id" +
            " where s.order.id = :idOrder")
    Page<OrderDetailReponse> orderDetailByOrderId(Pageable pageable, @Param("idOrder") Long idOrder);

    @Query(value = "SELECT NEW com.backend.dto.response.orderDetail.OrderDetailPDFResponse" +
            "(s.shoeDetail.code,s.shoeDetail.shoe.name,s.quantity,s.price)" +
            " FROM OrderDetail as s" +
            " left join ShoeDetail as b on s.shoeDetail.id = b.id" +
            " left join Thumbnail c on c.shoeDetail.id = b.id" +
            " where s.order.id = :idOrder")
    List<OrderDetailPDFResponse> orderDetailPDFByOrderId(@Param("idOrder") Long idOrder);

    @Query(value = "SELECT s.* FROM order_detail s where s.order_id = :idOrder and s.shoe_detail_id = :idShoeDetail", nativeQuery = true)
    OrderDetail orderDetailByOrderAndShoeDetail(@Param("idOrder") Long idOrder, @Param("idShoeDetail") Long idShoeDetail);

    @Query(value = "SELECT s.* FROM order_detail s where s.order_id = :idOrder", nativeQuery = true)
    List<OrderDetail> getAllOrderDetail(@Param("idOrder") Long idOrder);
}
