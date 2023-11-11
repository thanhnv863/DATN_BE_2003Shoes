package com.backend.repository;

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

    @Query(value = "select address.id,address.name,address.phone_number,address.specific_address,address.ward,\n" +
            "\t\taddress.district,address.province,address.note,address.default_address,address.account_id,\n" +
            "        account.role_id,account.name,account.email,account.avatar,account.created_time,\n" +
            "        account.updated_time,account.status\n" +
            "from account \n" +
            "join address on account.id = address.account_id where " +
            "address.default_address like concat('%', :defaultAddress, '%')", nativeQuery = true)
    List<Address> getAllAccountAndAddress(String defaultAddress);

    List<Address> findAddressesByAccount_Id(Long id);


}

