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

    @Query(value = "SELECT ad.id as idAddress,acc.id as idAccount,acc.role_id,acc.name as nameAccount,acc.code,acc.password,\n" +
            "\tacc.avatar,acc.created_time,acc.updated_time,acc.status,\n" +
            "    ad.name nameAddress,ad.phone_number,ad.specific_address,ad.ward,\n" +
            "    ad.district,ad.province,ad.note,ad.default_address\n" +
            " FROM account acc join address ad \n" +
            "\ton acc.id = ad.account_id\n" +
            "    where acc.id = :id",nativeQuery = true)
    List<Object[]> getOneAddressByAccountId(Long id);

    @Query(value = "SELECT * FROM address WHERE account_id = ?1 AND default_address = '1'", nativeQuery = true)
    Optional<Address> findByAccountIdAndDefaultAddress(Long accountId);
}

