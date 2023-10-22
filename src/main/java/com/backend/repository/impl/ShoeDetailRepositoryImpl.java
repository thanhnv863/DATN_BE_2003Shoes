package com.backend.repository.impl;

import com.backend.repository.ShoeDetailCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Repository
public class ShoeDetailRepositoryImpl implements ShoeDetailCustomRepository {
    @Autowired
    EntityManager entityManager;

    @Override
    public Page<Object> doSearch(Pageable pageable,String shoe, List<Float> size, List<String> category,
                                 List<String> brand, List<String> sole, List<String> color,
                                 BigDecimal minPrice, BigDecimal maxPrice) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.id, d.name as nameShoe, e.name as size, f.name as category,");
        sql.append(" g.name as brand, h.name as sole, i.name as color, a.code, a.qrcode,");
        sql.append(" a.price, a.quantity, a.created_time, a.updated_time,a.status,");
        sql.append(" c.img_url as thumbnail,GROUP_CONCAT(b.img_url) as images");
        sql.append(" FROM shoe_detail a JOIN image b ON a.id = b.shoe_detail_id");
        sql.append(" JOIN thumbnail c ON a.id = c.shoe_detail_id JOIN shoe d ON a.shoe_id = d.id");
        sql.append(" JOIN size e ON a.size_id = e.id JOIN category f ON a.category_id = f.id");
        sql.append(" JOIN brand g ON a.brand_id = g.id JOIN sole h ON a.sole_id = h.id");
        sql.append(" JOIN color i ON a.color_id = i.id WHERE 1 = 1");
//        sql.append("SELECT d.name, MAX(a.updated_time) as updated_time FROM shoe_detail a");
//        sql.append(" JOIN shoe d ON a.shoe_id = d.id GROUP BY d.name)");

        if (shoe != null) {
            sql.append(" AND (d.name like CONCAT('%', :shoe, '%'))");
        }

        if (size != null && size.size() > 0) {
            sql.append(" AND (e.name IN (:size))");
        }

        if (category != null && category.size() > 0) {
            sql.append(" AND (f.name IN (:category))");
        }

        if (brand != null && brand.size() > 0) {
            sql.append(" AND (g.name IN (:brand))");
        }

        if (sole != null && sole.size() > 0) {
            sql.append(" AND (h.name IN (:sole))");
        }

        if (color != null && color.size() > 0) {
            sql.append(" AND (i.name IN (:color))");
        }

        if (minPrice != null && maxPrice != null) {
            sql.append(" AND (a.price >= :minPrice AND a.price <= :maxPrice)");
        }

        sql.append(" GROUP BY a.id ORDER BY a.updated_time DESC");

        Query query = entityManager.createNativeQuery(sql.toString());

        if (shoe != null) {
            query.setParameter("shoe",shoe);
        }

        if (size != null && size.size() > 0){
            query.setParameter("size",size);
        }

        if (category != null && category.size() > 0) {
            query.setParameter("category",category);
        }

        if (brand != null && brand.size() > 0) {
            query.setParameter("brand",brand);
        }

        if (sole != null && sole.size() > 0) {
            query.setParameter("sole",sole);
        }

        if (color != null && color.size() > 0) {
            query.setParameter("color",color);
        }

        if (minPrice != null && maxPrice != null) {
            query.setParameter("minPrice",minPrice);
            query.setParameter("maxPrice",maxPrice);
        }

        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());
        List<Object> results = query.getResultList();

        long totalCount = getTotalCount(shoe,size, category, brand, sole, color,minPrice, maxPrice);
        return new PageImpl<>(results, pageable, totalCount);
    }

    @Override
    public long getTotalCount(String shoe,List<Float> size, List<String> category, List<String> brand, List<String> sole, List<String> color, BigDecimal minPrice, BigDecimal maxPrice) {
        StringBuilder countSql = new StringBuilder();
        countSql.append("SELECT COUNT(*) FROM (SELECT a.id FROM shoe_detail a JOIN image b ON a.id = b.shoe_detail_id");
        countSql.append(" JOIN thumbnail c ON a.id = c.shoe_detail_id JOIN shoe d ON a.shoe_id = d.id");
        countSql.append(" JOIN size e ON a.size_id = e.id JOIN category f ON a.category_id = f.id");
        countSql.append(" JOIN brand g ON a.brand_id = g.id JOIN sole h ON a.sole_id = h.id");
        countSql.append(" JOIN color i ON a.color_id = i.id WHERE 1 = 1");
//        countSql.append("SELECT d.name, MAX(a.updated_time) as updated_time FROM shoe_detail a");
//        countSql.append(" JOIN shoe d ON a.shoe_id = d.id GROUP BY d.name)");

        if (shoe != null) {
            countSql.append(" AND (d.name like CONCAT('%', :shoe, '%'))");
        }

        if (size != null && size.size() > 0) {
            countSql.append(" AND (e.name IN (:size))");
        }

        if (category != null && category.size() > 0) {
            countSql.append(" AND (f.name IN (:category))");
        }

        if (brand != null && brand.size() > 0) {
            countSql.append(" AND (g.name IN (:brand))");
        }

        if (sole != null && sole.size() > 0) {
            countSql.append(" AND (h.name IN (:sole))");
        }

        if (color != null && color.size() > 0) {
            countSql.append(" AND (i.name IN (:color))");
        }

        if (minPrice != null && maxPrice != null) {
            countSql.append(" AND (a.price >= :minPrice AND a.price <= :maxPrice)");
        }
        countSql.append(" GROUP BY a.id ORDER BY a.updated_time DESC ) AS subquery");

        Query query = entityManager.createNativeQuery(countSql.toString());

        if (shoe != null) {
            query.setParameter("shoe",shoe);
        }

        if (size != null && size.size() > 0){
            query.setParameter("size",size);
        }

        if (category != null && category.size() > 0) {
            query.setParameter("category",category);
        }

        if (brand != null && brand.size() > 0) {
            query.setParameter("brand",brand);
        }

        if (sole != null && sole.size() > 0) {
            query.setParameter("sole",sole);
        }

        if (color != null && color.size() > 0) {
            query.setParameter("color",color);
        }

        if (minPrice != null && maxPrice != null) {
            query.setParameter("minPrice",minPrice);
            query.setParameter("maxPrice",maxPrice);
        }

        return ((Number)query.getSingleResult()).longValue();
    }

    @Override
    public Object getOne(BigInteger id) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.id, d.name as nameShoe, e.name as size, f.name as category, \n" +
                "g.name as brand, h.name as sole, i.name as color, a.code, a.qrcode, \n" +
                "a.price, a.quantity, a.created_time, a.updated_time,a.status,\n" +
                "c.img_url as thumbnail,GROUP_CONCAT(b.img_url) as images \n" +
                "FROM shoe_detail a \n" +
                "JOIN image b ON a.id = b.shoe_detail_id \n" +
                "JOIN thumbnail c ON a.id = c.shoe_detail_id\n" +
                "JOIN shoe d ON a.shoe_id = d.id\n" +
                "JOIN size e ON a.size_id = e.id JOIN category f ON a.category_id = f.id\n" +
                "JOIN brand g ON a.brand_id = g.id JOIN sole h ON a.sole_id = h.id\n" +
                "JOIN color i ON a.color_id = i.id WHERE a.id = :id");
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("id",id);
        return query.getSingleResult();
    }

    @Override
    public List<Object> getListByCustom(String shoe, Float size, String category, String brand, String sole, String color, BigDecimal minPrice, BigDecimal maxPrice) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.id, d.name as nameShoe, e.name as size, f.name as category,");
        sql.append(" g.name as brand, h.name as sole, i.name as color, a.code, a.qrcode,");
        sql.append(" a.price, a.quantity, a.created_time, a.updated_time,a.status,");
        sql.append(" c.img_url as thumbnail,GROUP_CONCAT(b.img_url) as images");
        sql.append(" FROM shoe_detail a JOIN image b ON a.id = b.shoe_detail_id");
        sql.append(" JOIN thumbnail c ON a.id = c.shoe_detail_id JOIN shoe d ON a.shoe_id = d.id");
        sql.append(" JOIN size e ON a.size_id = e.id JOIN category f ON a.category_id = f.id");
        sql.append(" JOIN brand g ON a.brand_id = g.id JOIN sole h ON a.sole_id = h.id");
        sql.append(" JOIN color i ON a.color_id = i.id WHERE 1 = 1");

        if (shoe != null) {
            sql.append(" AND (d.name like CONCAT('%', :shoe, '%'))");
        }

        if (size != null ) {
            sql.append(" AND (e.name = :size)");
        }

        if (category != null) {
            sql.append(" AND (f.name = :category)");
        }

        if (brand != null ) {
            sql.append(" AND (g.name = :brand)");
        }

        if (sole != null) {
            sql.append(" AND (h.name = :sole)");
        }

        if (color != null) {
            sql.append(" AND (i.name = :color)");
        }

        if (minPrice != null && maxPrice != null) {
            sql.append(" AND (a.price >= :minPrice AND a.price <= :maxPrice)");
        }

        sql.append(" GROUP BY a.id ORDER BY a.updated_time DESC");

        Query query = entityManager.createNativeQuery(sql.toString());

        if (shoe != null) {
            query.setParameter("shoe",shoe);
        }

        if (size != null){
            query.setParameter("size",size);
        }

        if (category != null) {
            query.setParameter("category",category);
        }

        if (brand != null) {
            query.setParameter("brand",brand);
        }

        if (sole != null) {
            query.setParameter("sole",sole);
        }

        if (color != null) {
            query.setParameter("color",color);
        }

        if (minPrice != null && maxPrice != null) {
            query.setParameter("minPrice",minPrice);
            query.setParameter("maxPrice",maxPrice);
        }

        List<Object> results = query.getResultList();
        return results;
    }
}
