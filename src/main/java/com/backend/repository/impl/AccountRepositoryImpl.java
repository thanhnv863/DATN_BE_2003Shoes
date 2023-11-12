package com.backend.repository.impl;

import com.backend.repository.AccountCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
@Repository
public class AccountRepositoryImpl implements AccountCustomRepository {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<Object> doSearch(Pageable pageable, String name, String email, Integer role, Integer statusAccount) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.id, a.code, a.name, a.email, a.avatar, a.role_id, a.status");
        sql.append(" FROM account a");
        sql.append(" left join address b on a.id = b.account_id");
        sql.append(" left join role c on a.role_id = c.id");
        sql.append(" WHERE 1 = 1");
        sql.append(" AND (:name IS NULL OR a.code LIKE CONCAT('%', :name, '%') OR a.name LIKE CONCAT('%', :name, '%'))");
        sql.append(" AND (:email IS NULL OR a.email = :email)");
        sql.append(" AND (:role IS NULL OR a.role_id = :role)");
        sql.append(" AND (:statusAccount IS NULL OR a.status = :statusAccount)");
        sql.append(" Group by a.id,a.code,a.name,a.email, a.avatar, a.role_id, a.status");
        sql.append(" ORDER BY a.created_time");
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("name", name);
        query.setParameter("email", email);
        query.setParameter("role", role);
        query.setParameter("statusAccount", statusAccount);

        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        List<Object> results = query.getResultList();
        long totalCount = getTotalCount(name, email, role, statusAccount);

        return new PageImpl<>(results, pageable, totalCount);
    }
    @Override
    public long getTotalCount(String name, String email, Integer role, Integer statusAccount) {
        StringBuilder countSql = new StringBuilder();
        countSql.append("SELECT count(*) from (");
        countSql.append("SELECT DISTINCT a.id from account as a");
        countSql.append(" LEFT join address b on a.id = b.account_id");
        countSql.append(" LEFT join role c on a.role_id = c.id");
        countSql.append(" WHERE 1 = 1");
        countSql.append(" AND (:name IS NULL OR a.code LIKE CONCAT('%', :name, '%') OR a.name LIKE CONCAT('%', :name, '%'))");
        countSql.append(" AND (:email IS NULL OR a.email = :email)");
        countSql.append(" AND (:role IS NULL OR a.role_id = :role)");
        countSql.append(" AND (:statusAccount IS NULL OR a.status = :statusAccount)");
        countSql.append(" Group by a.id,a.code,a.name,a.email, a.avatar, a.role_id, a.status");
        countSql.append(") as subquery");

        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        countQuery.setParameter("name", name);
        countQuery.setParameter("email", email);
        countQuery.setParameter("role", role);
        countQuery.setParameter("statusAccount", statusAccount);
        return ((Number) countQuery.getSingleResult()).longValue();
    }
}
