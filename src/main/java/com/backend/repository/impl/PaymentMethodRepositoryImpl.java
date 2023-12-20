package com.backend.repository.impl;

import com.backend.repository.PaymentMethodCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@Repository
public class PaymentMethodRepositoryImpl implements PaymentMethodCustomRepository {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<Object> doSearch(Pageable pageable,
                                 String orderCode, String method,
                                 BigDecimal priceMin, BigDecimal priceMax,
                                 Date dateFirst, Date dateLast, Integer status) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.id, b.code, a.method, a.total, a.payment_time, ");
        sql.append(" a.note, a.status FROM payment_method as a ");
        sql.append(" LEFT JOIN my_order b on a.order_id = b.id");
        sql.append(" where 1 = 1 ");
        if (orderCode != null) {
            sql.append(" AND (b.code = :orderCode) ");
        }
        if (method != null) {
            sql.append(" AND (a.method = :method) ");
        }
        if (priceMin != null && priceMax != null) {
            sql.append(" AND (a.total >= :priceMin AND a.total <= :priceMax) ");
        }
        if (dateFirst != null && dateLast != null) {
            sql.append(" AND (a.payment_time BETWEEN :dateFirst AND :dateLast) ");
        }
        if (status != null) {
            sql.append(" AND (a.status = :status) ");
        }
//        sql.append(" GROUP BY a.id, b.code, a.method, a.total, a.payment_time, a.note, a.status FROM payment_method");
        sql.append(" ORDER BY a.payment_time desc");

        Query query = entityManager.createNativeQuery(sql.toString());
        if (orderCode != null) {
            query.setParameter("orderCode", orderCode);
        }

        if (method != null) {
            query.setParameter("method", method);
        }
        if (priceMin != null && priceMax != null) {
            query.setParameter("priceMin", priceMin);
            query.setParameter("priceMax", priceMax);
        }
        if (dateFirst != null && dateLast != null) {
            query.setParameter("dateFirst", dateFirst);
            query.setParameter("dateLast", dateLast);
        }
        if (status != null) {
            query.setParameter("status", status);
        }

        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        List<Object> results = query.getResultList();
        long totalCount = getTotalCount(orderCode, method,
                priceMin, priceMax,
                dateFirst, dateLast, status
        );

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public long getTotalCount(String orderCode, String method,
                              BigDecimal priceMin, BigDecimal priceMax,
                              Date dateFirst, Date dateLast, Integer status
    ) {
        StringBuilder countSql = new StringBuilder();
        countSql.append("SELECT count(*) from (");
        countSql.append("SELECT DISTINCT a.id  FROM payment_method as a");
        countSql.append(" LEFT JOIN my_order b on a.order_id = b.id");
        countSql.append(" where 1 = 1 ");
        if (orderCode != null) {
            countSql.append(" AND (b.code = :orderCode) ");
        }
        if (method != null) {
            countSql.append(" AND (a.method = :method) ");
        }
        if (priceMin != null && priceMax != null) {
            countSql.append(" AND (a.total >= :priceMin AND a.total <= :priceMax) ");
        }
        if (dateFirst != null && dateLast != null) {
            countSql.append(" AND (a.payment_time BETWEEN :dateFirst AND :dateLast) ");
        }
        if (status != null) {
            countSql.append(" AND (a.status = :status) ");
        }
        countSql.append(") as subquery");

        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        if (orderCode != null) {
            countQuery.setParameter("orderCode", orderCode);
        }

        if (method != null) {
            countQuery.setParameter("method", method);
        }
        if (priceMin != null && priceMax != null) {
            countQuery.setParameter("priceMin", priceMin);
            countQuery.setParameter("priceMax", priceMax);
        }
        if (dateFirst != null && dateLast != null) {
            countQuery.setParameter("dateFirst", dateFirst);
            countQuery.setParameter("dateLast", dateLast);
        }
        if (status != null) {
            countQuery.setParameter("status", status);
        }
        return ((Number) countQuery.getSingleResult()).longValue();
    }
}
