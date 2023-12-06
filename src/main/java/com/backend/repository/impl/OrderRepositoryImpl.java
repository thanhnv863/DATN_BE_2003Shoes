package com.backend.repository.impl;

import com.backend.repository.OrderCustomRepository;
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
public class OrderRepositoryImpl implements OrderCustomRepository {
    @Autowired
    EntityManager entityManager;

    @Override
    public Page<Object> doSearch(Pageable pageable,
                                 String type, String voucher, String customer,
                                 Date dateFirst, Date dateLast, Integer status,
                                 BigDecimal priceMin, BigDecimal priceMax) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.id, b.name, a.code, a.type, a.customer_name, a.phone_number,");
        sql.append(" a.address, a.ship_fee, a.money_reduce, a.total_money, a.create_date,");
        sql.append(" a.pay_date, a.ship_date, a.desired_date, a.receive_date, a.created_by,");
        sql.append(" a.updated_by,a.note, a.status FROM my_order as a ");
        sql.append(" LEFT JOIN voucher b on a.voucher_id = b.id");
        sql.append(" LEFT JOIN account c on a.account_id = c.id");
        sql.append(" LEFT JOIN address d on c.id = d.account_id");
        sql.append(" where 1 = 1 ");
        if (type != null) {
            sql.append(" AND (a.type = :type) ");
        }
        if (voucher != null) {
            sql.append(" AND (b.name = :voucher) ");
        }
        if (customer != null) {
            sql.append(" AND (a.customer_name like CONCAT('%', :customer, '%') OR a.phone_number like CONCAT('%', :customer, '%')) ");
        }
        if (dateFirst != null && dateLast != null) {
            sql.append(" AND (a.create_date BETWEEN :dateFirst AND :dateLast) ");
        }
        if (status != null) {
            sql.append(" AND (a.status = :status) ");
        }
        if (priceMin != null && priceMax != null) {
            sql.append(" AND (a.total_money >= :priceMin AND a.total_money <= :priceMax) ");
        }

        sql.append(" GROUP BY a.id");
        sql.append(" ORDER BY a.create_date desc, a.pay_date desc, a.ship_date desc, a.desired_date desc, a.receive_date desc");

        Query query = entityManager.createNativeQuery(sql.toString());
        if (type != null) {
            query.setParameter("type", type);
        }

        if (voucher != null) {
            query.setParameter("voucher", voucher);
        }
        if (customer != null) {
            query.setParameter("customer", customer);
        }
        if (dateFirst != null && dateLast != null) {
            query.setParameter("dateFirst", dateFirst);
            query.setParameter("dateLast", dateLast);
        }
        if (status != null) {
            query.setParameter("status", status);
        }
        if (priceMin != null && priceMax != null) {
            query.setParameter("priceMin", priceMin);
            query.setParameter("priceMax", priceMax);
        }

        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        List<Object> results = query.getResultList();
        long totalCount = getTotalCount(type, voucher, customer,
                dateFirst, dateLast, status,
                priceMin, priceMax
        );

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public long getTotalCount(String type, String voucher, String customer,
                              Date dateFirst, Date dateLast, Integer status,
                              BigDecimal priceMin, BigDecimal priceMax
    ) {
        StringBuilder countSql = new StringBuilder();
        countSql.append("SELECT count(*) from (");
        countSql.append("SELECT DISTINCT a.id  FROM my_order as a");
        countSql.append(" LEFT JOIN voucher b on a.voucher_id = b.id");
        countSql.append(" LEFT JOIN account c on a.account_id = c.id");
        countSql.append(" LEFT JOIN address d on c.id = d.account_id");
        countSql.append(" where 1 = 1 ");
        if (type != null) {
            countSql.append(" AND (a.type = :type) ");
        }
        if (voucher != null) {
            countSql.append(" AND (b.name = :voucher) ");
        }
        if (customer != null) {
            countSql.append(" AND (a.customer_name like CONCAT('%', :customer, '%') OR a.phone_number like CONCAT('%', :customer, '%')) ");
        }
        if (dateFirst != null && dateLast != null) {
            countSql.append(" AND (a.create_date BETWEEN :dateFirst AND :dateLast) ");
        }
        if (status != null) {
            countSql.append(" AND (a.status = :status) ");
        }
        if (priceMin != null && priceMax != null) {
            countSql.append(" AND (a.total_money >= :priceMin AND a.total_money <= :priceMax) ");
        }
        countSql.append(") as subquery");

        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        if (type != null) {
            countQuery.setParameter("type", type);
        }
        if (voucher != null) {
            countQuery.setParameter("voucher", voucher);
        }
        if (customer != null) {
            countQuery.setParameter("customer", customer);
        }
        if (dateFirst != null && dateLast != null) {
            countQuery.setParameter("dateFirst", dateFirst);
            countQuery.setParameter("dateLast", dateLast);
        }
        if (status != null) {
            countQuery.setParameter("status", status);
        }
        if (priceMin != null && priceMax != null) {
            countQuery.setParameter("priceMin", priceMin);
            countQuery.setParameter("priceMax", priceMax);
        }
        return ((Number) countQuery.getSingleResult()).longValue();
    }

    @Override
    public List<Object> getListExport(String type, String voucher, String customer, Date dateFirst, Date dateLast, Integer status, BigDecimal priceMin, BigDecimal priceMax) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.id, b.name, a.code, a.type, a.customer_name, a.phone_number,");
        sql.append(" a.address, a.ship_fee, a.money_reduce, a.total_money, a.create_date,");
        sql.append(" a.pay_date, a.ship_date, a.desired_date, a.receive_date, a.created_by,");
        sql.append(" a.updated_by,a.note, a.status FROM my_order as a ");
        sql.append(" LEFT JOIN voucher b on a.voucher_id = b.id");
        sql.append(" LEFT JOIN account c on a.account_id = c.id");
        sql.append(" LEFT JOIN address d on c.id = d.account_id");
        sql.append(" where 1 = 1 ");
        if (type != null) {
            sql.append(" AND (a.type = :type) ");
        }
        if (voucher != null) {
            sql.append(" AND (b.name = :voucher) ");
        }
        if (customer != null) {
            sql.append(" AND (a.customer_name like CONCAT('%', :customer, '%') OR a.phone_number like CONCAT('%', :customer, '%')) ");
        }
        if (dateFirst != null && dateLast != null) {
            sql.append(" AND (a.create_date BETWEEN :dateFirst AND :dateLast) ");
        }
        if (status != null) {
            sql.append(" AND (a.status = :status) ");
        }
        if (priceMin != null && priceMax != null) {
            sql.append(" AND (a.total_money >= :priceMin AND a.total_money <= :priceMax) ");
        }

//        sql.append(" GROUP BY a.name, a.code, a.grade_level, a.type, a.sup_type, a.description, c.ord, c.name");
        sql.append(" ORDER BY a.create_date desc, a.pay_date desc, a.ship_date desc, a.desired_date desc, a.receive_date desc");

        Query query = entityManager.createNativeQuery(sql.toString());
        if (type != null) {
            query.setParameter("type", type);
        }

        if (voucher != null) {
            query.setParameter("voucher", voucher);
        }
        if (customer != null) {
            query.setParameter("customer", customer);
        }
        if (dateFirst != null && dateLast != null) {
            query.setParameter("dateFirst", dateFirst);
            query.setParameter("dateLast", dateLast);
        }
        if (status != null) {
            query.setParameter("status", status);
        }
        if (priceMin != null && priceMax != null) {
            query.setParameter("priceMin", priceMin);
            query.setParameter("priceMax", priceMax);
        }
        List<Object> results = query.getResultList();
        return results;
    }
}
