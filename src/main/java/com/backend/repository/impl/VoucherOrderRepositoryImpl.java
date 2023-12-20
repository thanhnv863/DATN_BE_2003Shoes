package com.backend.repository.impl;

import com.backend.repository.VoucherOrderCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class VoucherOrderRepositoryImpl implements VoucherOrderCustomRepository {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<Object> doSearch(Pageable pageable, String voucher, Integer status, LocalDateTime startDate, LocalDateTime endDate) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT v.id,v.code,v.name,v.quantity,v.min_order_value,v.discount_amount,v.maximum_reduction_value,v.start_date," +
                " v.end_date,v.created_time,v.updated_time,v.reduce_form,v.status FROM voucher as v");
        sql.append(" WHERE 1 = 1");

        if (voucher != null) {
            sql.append(" AND (v.name like CONCAT('%', :voucher, '%'))");
        }

        if (status != null) {
            sql.append(" AND (v.status = :status)");
        }
//        else {
//            // Thêm điều kiện chỉ lấy status = 0, 1 hoặc 2
//            sql.append(" AND (v.status IN (0, 1, 2))");
//        }

        if (startDate != null && endDate != null) {
            sql.append(" AND (v.start_date >= :startDate AND v.end_date <= :endDate)");
        } else if (startDate != null) {
            sql.append(" AND (v.start_date >= :startDate)");
        } else if (endDate != null) {
            sql.append(" AND (v.end_date <= :endDate)");
        }

        sql.append(" ORDER BY v.updated_time DESC");
        Query query = entityManager.createNativeQuery(sql.toString());

        if (voucher != null) {
            query.setParameter("voucher", voucher);
        }

        if (status != null) {
            query.setParameter("status", status);
        }

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        List<Object> results = query.getResultList();
        long totalCount = getTotalCount(voucher, status,startDate,endDate);

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public long getTotalCount(String voucher, Integer status, LocalDateTime startDate, LocalDateTime endDate) {
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

    @Override
    public List<Object> doSearchMinBillValue(BigDecimal totalMoneyMyOrder) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT v.id, v.code, v.name, v.quantity, v.min_order_value, v.discount_amount, v.maximum_reduction_value, v.start_date, ");
        sql.append(" v.end_date, v.created_time, v.updated_time, v.reduce_form, v.status FROM voucher as v");
        sql.append(" WHERE v.status=1");

        if (totalMoneyMyOrder != null) {
            sql.append(" AND (v.min_order_value <= :totalMoneyMyOrder)");
        }

        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("totalMoneyMyOrder", totalMoneyMyOrder);

        List<Object> results = query.getResultList();
        return results;

    }
    @Override
    public List<Object> searchExportListVoucher( String voucher, Integer status, LocalDateTime startDate, LocalDateTime endDate) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT v.id,v.code,v.name,v.quantity,v.min_order_value,v.discount_amount,v.maximum_reduction_value,v.start_date," +
                " v.end_date,v.created_time,v.updated_time,v.reduce_form,v.status FROM voucher as v");
        sql.append(" WHERE 1 = 1");

        if (voucher != null) {
            sql.append(" AND (v.name like CONCAT('%', :voucher, '%'))");
        }

        if (status != null) {
            sql.append(" AND (v.status = :status)");
        }
//        else {
//            // Thêm điều kiện chỉ lấy status = 0, 1 hoặc 2
//            sql.append(" AND (v.status IN (0, 1, 2))");
//        }

        if (startDate != null && endDate != null) {
            sql.append(" AND (v.start_date >= :startDate AND v.end_date <= :endDate)");
        } else if (startDate != null) {
            sql.append(" AND (v.start_date >= :startDate)");
        } else if (endDate != null) {
            sql.append(" AND (v.end_date <= :endDate)");
        }

        Query query = entityManager.createNativeQuery(sql.toString());

        if (voucher != null) {
            query.setParameter("voucher", voucher);
        }

        if (status != null) {
            query.setParameter("status", status);
        }

        if (startDate != null) {
            query.setParameter("startDate", startDate);
        }

        if (endDate != null) {
            query.setParameter("endDate", endDate);
        }

        List<Object> results = query.getResultList();
        return results;
    }
}
