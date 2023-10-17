package com.backend.repository.impl;

import com.backend.repository.VoucherOrderCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class VoucherOrderRepositoryImpl implements VoucherOrderCustomRepository {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<Object> doSearch(Pageable pageable, String voucher, Integer status) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT v.id,v.code,v.name, v.quantity,v.min_order_value,v.discount_amount,v.start_date," +
                " v.end_date,v.created_time,v.updated_time,v.reduce_form,v.status FROM voucher as v");
        sql.append(" WHERE 1 = 1");

        if (voucher != null) {
            sql.append(" AND (v.name like CONCAT('%', :voucher, '%'))");
        }

        if (status != null) {
            sql.append(" AND (v.status = :status)");
        }

        Query query = entityManager.createNativeQuery(sql.toString());

        if (voucher != null) {
            query.setParameter("voucher", voucher);
        }

        if (status != null) {
            query.setParameter("status", status);
        }

        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        List<Object> results = query.getResultList();
        long totalCount = getTotalCount(voucher, status);

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public long getTotalCount(String voucher, Integer status) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT count(*) from (");
        sql.append(" SELECT DISTINCT v.id  FROM voucher as v");
        sql.append(" WHERE 1 = 1 ");

        if (voucher != null) {
            sql.append(" AND (v.name like CONCAT('%', :voucher, '%'))");
        }

        if (status != null) {
            sql.append(" AND (v.status = :status)");
        }

        sql.append(") AS subquery");

        Query countQuery = entityManager.createNativeQuery(sql.toString());

        if (voucher != null) {
            countQuery.setParameter("voucher", voucher);
        }

        if (status != null) {
            countQuery.setParameter("status", status);
        }

        return ((Number) countQuery.getSingleResult()).longValue();
    }
}
