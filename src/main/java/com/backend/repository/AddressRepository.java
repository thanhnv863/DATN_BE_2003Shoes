package com.backend.repository;

import com.backend.dto.response.AddressResponse;
import com.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {


    @Query("select a from Address a where " +
            " a.name like concat('%', :name, '%')")
    List<Address> searchNameClient(String name);

    @Query(value = "select * from account join address on account.id = address.account_id", nativeQuery = true)
    List<Address> getAllAccountAndAddress();
}

