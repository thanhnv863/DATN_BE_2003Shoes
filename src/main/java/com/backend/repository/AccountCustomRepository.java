package com.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;

public interface AccountCustomRepository {
    Page<Object> doSearch(Pageable pageable, String name,
                          String email, Integer role, Integer statusAccount
                          );
    long getTotalCount(String name, String email, Integer role, Integer statusAccount);
}
