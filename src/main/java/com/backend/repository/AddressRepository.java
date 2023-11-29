package com.backend.repository;

import com.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {

    List<Address> findAddressesByAccount_Id(Long id);

    @Query(value = "SELECT acc.id,acc.role_id,acc.name as nameAccount,acc.code,acc.password,\n" +
            "\tacc.avatar,acc.created_time,acc.updated_time,acc.status,\n" +
            "    ad.name nameAddress,ad.phone_number,ad.specific_address,ad.ward,\n" +
            "    ad.district,ad.province,ad.note,ad.default_address\n" +
            " FROM account acc left join address ad \n" +
            "\ton acc.id = ad.account_id\n" +
            "    where acc.id = :id and ad.default_address = 0",nativeQuery = true)
    List<Object[]> getAllAccountAndAddress(Long id);

}

