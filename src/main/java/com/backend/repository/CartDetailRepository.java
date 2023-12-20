package com.backend.repository;

import com.backend.ServiceResult;
import com.backend.dto.request.CartDetailRequest;
import com.backend.entity.Cart;
import com.backend.entity.CartDetail;
import com.backend.entity.ShoeDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail,Long> {
    CartDetail findByCartAndShoeDetail(Cart cart, ShoeDetail shoeDetail);

    List<CartDetail> findByCart(Cart cart);

    @Query(value = "Select * from cart_detail where cart_detail.cart_id = ?1 and cart_detail.status = 1 or cart_detail.status = 2", nativeQuery = true)
    List<CartDetail> listCartDetailByStatus(Long idCart);


    @Query(value = "Select * from cart_detail where cart_detail.cart_id = ?1", nativeQuery = true)
    List<CartDetail> listCartDetailAll(Long idCart);

    @Transactional
    @Modifying
    void deleteCartDetailByStatus(Integer status);
}
