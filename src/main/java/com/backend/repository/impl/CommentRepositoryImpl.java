package com.backend.repository.impl;

import com.backend.repository.CommentCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class CommentRepositoryImpl implements CommentCustomRepository {
    @Autowired
    private EntityManager entityManager;

    @Override
    public Page<Object> doSearch(Pageable pageable, Long idShoeDetail) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.id,b.name, a.stars, a.content, a.date");
        sql.append("  FROM comment as a");
        sql.append(" join account as b on a.account_id = b.id");
        sql.append(" join shoe_detail as c on c.id = a.shoe_detail_id");
        sql.append(" WHERE 1 = 1");
        sql.append(" AND (:idShoeDetail IS NULL OR a.shoe_detail_id = :idShoeDetail)");
        sql.append(" ORDER BY a.date desc");
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("idShoeDetail", idShoeDetail);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        List<Object> results = query.getResultList();
        long totalCount = getTotalCount(idShoeDetail);

        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public long getTotalCount(Long idShoeDetail) {
        StringBuilder countSql = new StringBuilder();
        countSql.append("SELECT count(*) from (");
        countSql.append("SELECT DISTINCT a.id FROM comment as a");
        countSql.append(" join account as b on a.account_id = b.id");
        countSql.append(" join shoe_detail as c on c.id = a.shoe_detail_id");
        countSql.append(" WHERE 1 = 1");
        countSql.append(" AND (:idShoeDetail IS NULL OR a.shoe_detail_id = :idShoeDetail)");
        countSql.append(" ORDER BY a.date desc");
        countSql.append(") as subquery");

        Query countQuery = entityManager.createNativeQuery(countSql.toString());
        countQuery.setParameter("idShoeDetail", idShoeDetail);
        return ((Number) countQuery.getSingleResult()).longValue();
    }

    @Override
    public List<Object> top3() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.id,b.name, a.stars, a.content, a.date");
        sql.append("  FROM comment as a");
        sql.append(" join account as b on a.account_id = b.id");
        sql.append(" join shoe_detail as c on c.id = a.shoe_detail_id");
        sql.append(" WHERE 1 = 1 and a.stars = 5");
        sql.append(" ORDER BY a.date desc limit 3");
        Query query = entityManager.createNativeQuery(sql.toString());
        List<Object> results = query.getResultList();
        return results;
    }
}
