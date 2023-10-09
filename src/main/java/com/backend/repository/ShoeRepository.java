package com.backend.repository;

import com.backend.entity.Account;
import com.backend.entity.Shoe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.Optional;

@Repository
public interface ShoeRepository extends JpaRepository<Shoe, Long> {
    @Query("SELECT s FROM Shoe s WHERE s.name = :nameShoe")
    Optional<Shoe> findByNameShoe(@Param("nameShoe") String nameShoe);
}
