package com.backend.repository;

import com.backend.dto.response.CommentResponse;
import com.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    @Query(value = "SELECT NEW com.backend.dto.response.CommentResponse (a.id,a.account.name,a.stars, a.content, a.date)  " +
            "FROM Comment as a " +
            "join Account as b on a.account.id = b.id " +
            "join ShoeDetail as c on c.id = a.shoeDetail.id " +
            "WHERE 1 = 1 " +
            "AND (:idOrder IS NULL OR a.order.id = :idOrder) " +
            "AND (:idShoeDetail IS NULL OR a.shoeDetail.id = :idShoeDetail) " +
            "AND a.status = 1")
    CommentResponse getOne(@Param("idOrder") Long idOrder, @Param("idShoeDetail") Long idShoeDetail);
}
