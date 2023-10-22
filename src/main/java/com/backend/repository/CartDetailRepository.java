package com.backend.repository;

import com.backend.ServiceResult;
import com.backend.dto.request.CartDetailRequest;
import com.backend.entity.Cart;
import com.backend.entity.CartDetail;
import com.backend.entity.ShoeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail,Long> {
    CartDetail findByCartAndShoeDetail(Cart cart, ShoeDetail shoeDetail);

    List<CartDetail> findByCart(Cart cart);
}
