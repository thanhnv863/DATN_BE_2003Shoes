package com.backend.repository;

import com.backend.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByName(String username);

    @Query("SELECT a FROM Account a JOIN a.role r WHERE a.email = :email")
    Optional<Account> findByEmail(@Param("email") String email);

    @Query("select a from Account a where " +
            " a.name like concat('%', :name, '%')")
    List<Account> searchNameStaff(String name);

    @Query(value = "select * from account acc left join address ad on acc.id = ad.account_id\n" +
            "\t\t\t\t\t\tleft join role r on acc.role_id = r.id", nativeQuery = true)
    Page<Account> getAllAccount(Pageable pageable);

    @Query("select a from Account a where " +
            " a.status = 1 ")
    List<Account> getAllAccount();

    @Query(value = "SELECT * FROM account \n" +
            "where account.email = ?1 and account.role_id = 2", nativeQuery = true)
    Optional<Account> getOneByEmail(String email);

    @Query(value = "SELECT * FROM account \n" +
            "where account.role_id = ?1", nativeQuery = true)
    List<Account> getListByRole(Integer role);

    @Query(value = "SELECT acc.id,acc.role_id,acc.name as nameAccount,acc.code,acc.password,\n" +
            "\tacc.avatar,acc.created_time,acc.updated_time,acc.status,\n" +
            "    ad.name nameAddress,ad.phone_number,ad.specific_address,ad.ward,\n" +
            "    ad.district,ad.province,ad.note,ad.default_address\n" +
            " FROM account acc left join address ad \n" +
            "\ton acc.id = ad.account_id\n" +
            "    where acc.id = :id and ad.default_address = 0",nativeQuery = true)
    List<Object[]> getAllAccountAndAddress(Long id);
}
