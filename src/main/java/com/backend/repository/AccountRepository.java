package com.backend.repository;

import com.backend.entity.Account;
import com.backend.entity.Address;
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

    @Query("select a from Account a where " +
            " a.status = 1 ")
    List<Account> getAllAccount();
}
