package com.backend.repository.impl;

import com.backend.repository.ShoeDetailCustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Repository
public class ShoeDetailRepositoryImpl implements ShoeDetailCustomRepository {
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Page<Object> doSearch(Pageable pageable,String shoe, List<Float> size, List<String> category,
                                 List<String> brand, List<String> sole, List<String> color,
                                 BigDecimal minPrice, BigDecimal maxPrice, String sort) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.id, d.name as nameShoe, e.name as size, f.name as category,");
        sql.append(" g.name as brand, h.name as sole, i.name as color, a.code, a.qrcode,");
        sql.append(" a.price, a.quantity, a.created_time, a.updated_time,a.status,");
        sql.append(" c.img_url as thumbnail,GROUP_CONCAT(b.img_url) as images");
        sql.append(" FROM shoe_detail a JOIN image b ON a.id = b.shoe_detail_id");
        sql.append(" JOIN thumbnail c ON a.id = c.shoe_detail_id JOIN shoe d ON a.shoe_id = d.id");
        sql.append(" JOIN size e ON a.size_id = e.id JOIN category f ON a.category_id = f.id");
        sql.append(" JOIN brand g ON a.brand_id = g.id JOIN sole h ON a.sole_id = h.id");
        sql.append(" JOIN color i ON a.color_id = i.id WHERE 1 = 1 AND a.status = 1");
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

        if (sort.equalsIgnoreCase("-updatedAt")){
            sql.append(" GROUP BY a.id ORDER BY a.updated_time DESC");
        }

        if (sort.equalsIgnoreCase("-price")){
            sql.append(" GROUP BY a.id ORDER BY a.price DESC");
        }

        if (sort.equalsIgnoreCase("price")){
            sql.append(" GROUP BY a.id ORDER BY a.price ASC");
        }

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
    public List<Object> getListByCustom(String shoe, Float size, String category, String brand, String sole,
                                        String color, BigDecimal minPrice, BigDecimal maxPrice, Integer status) {
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

        if (status != null) {
            sql.append(" AND (a.status = :status)");
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

        if (status != null) {
            query.setParameter("status",status);
        }

        List<Object> results = query.getResultList();
        return results;
    }

    @Override
    public List<Object[]> getListSizeByShoeNameId(Long idShoe, Long idColor) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT MIN(size.name) AS size_name, MIN(sd.shoe_id) AS shoe_id, MIN(sd.code) AS code, MIN(sd.id) AS idSD");
        sql.append("  FROM shoe_detail sd");
        sql.append(" INNER JOIN size ON sd.size_id = size.id");
        sql.append(" INNER JOIN shoe ON sd.shoe_id = shoe.id");
        sql.append(" INNER JOIN color ON sd.color_id = color.id");
        sql.append(" WHERE 1 = 1");
        sql.append(" AND (:idShoe IS NULL OR shoe.id = :idShoe)");
        sql.append(" AND (:idColor IS NULL OR color_id = :idColor)");
        sql.append(" GROUP BY size.name");
        sql.append(" ORDER BY size_name ASC");
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("idShoe", idShoe);
        query.setParameter("idColor", idColor);
        List<Object[]> results = query.getResultList();
        return results;
    }

    @Override
    public List<Object[]> getListTop4BestSale() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.id, d.name as nameShoe, e.name as sizeName, f.name as categoryName, g.name as brandName,");
        sql.append(" h.name as soleName, i.name as colorName, a.code,");
        sql.append("  a.qrcode, a.price, a.quantity, a.created_time, a.updated_time, a.status,");
        sql.append(" c.img_url as thumbnail,GROUP_CONCAT(b.img_url) as images");
        sql.append(" FROM shoe_detail a");
        sql.append(" JOIN image b ON a.id = b.shoe_detail_id");
        sql.append(" JOIN thumbnail c ON a.id = c.shoe_detail_id");
        sql.append(" JOIN shoe d ON a.shoe_id = d.id");
        sql.append(" JOIN size e ON a.size_id = e.id");
        sql.append(" JOIN category f ON a.category_id = f.id");
        sql.append(" JOIN brand g ON a.brand_id = g.id");
        sql.append(" JOIN sole h ON a.sole_id = h.id");
        sql.append(" JOIN color i ON a.color_id = i.id");
        sql.append(" JOIN (");
        sql.append(" SELECT shoe_detail_id");
        sql.append(" FROM order_detail");
        sql.append(" WHERE status = 1");
        sql.append(" GROUP BY shoe_detail_id");
        sql.append(" ORDER BY COUNT(*) DESC");
        sql.append(" LIMIT 4");
        sql.append(" ) AS top_shoes ON a.id = top_shoes.shoe_detail_id");
        sql.append(" GROUP BY a.id, d.name");
        sql.append(" ORDER BY a.updated_time DESC");
        Query query = entityManager.createNativeQuery(sql.toString());
        List<Object[]> results = query.getResultList();
        return results;
    }

    @Override
    public List<Object[]> getListTop4Newest() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.id, d.name as nameShoe, e.name as sizeName, f.name as categoryName, g.name as brandName,");
        sql.append("  h.name as soleName, i.name as colorName, a.code,");
        sql.append(" a.qrcode, a.price, a.quantity, a.created_time, a.updated_time, a.status,");
        sql.append(" c.img_url as thumbnail,GROUP_CONCAT(b.img_url) as images");
        sql.append(" FROM shoe_detail a JOIN image b ON a.id = b.shoe_detail_id");
        sql.append(" JOIN thumbnail c ON a.id = c.shoe_detail_id JOIN shoe d ON a.shoe_id = d.id");
        sql.append(" JOIN size e ON a.size_id = e.id JOIN category f ON a.category_id = f.id");
        sql.append(" JOIN brand g ON a.brand_id = g.id JOIN sole h ON a.sole_id = h.id");
        sql.append(" JOIN color i ON a.color_id = i.id JOIN order_detail k ON k.shoe_detail_id = a.id");
        sql.append(" GROUP BY d.name ORDER BY MAX(a.created_time) DESC LIMIT 4");
        Query query = entityManager.createNativeQuery(sql.toString());
        List<Object[]> results = query.getResultList();
        return results;
    }

    @Override
    public List<Object[]> getListVersionOfShoe(Long idShoe) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT a.id, d.name as nameShoe, e.name as sizeName, f.name as categoryName, g.name as brandName,");
        sql.append("  h.name as soleName, i.name as colorName, a.code,");
        sql.append(" a.qrcode, a.price, a.quantity, a.created_time, a.updated_time, a.status,");
        sql.append(" c.img_url as thumbnail,GROUP_CONCAT(b.img_url) as images");
        sql.append(" FROM shoe_detail a JOIN image b ON a.id = b.shoe_detail_id");
        sql.append(" JOIN thumbnail c ON a.id = c.shoe_detail_id JOIN shoe d ON a.shoe_id = d.id");
        sql.append(" JOIN size e ON a.size_id = e.id JOIN category f ON a.category_id = f.id");
        sql.append(" JOIN brand g ON a.brand_id = g.id JOIN sole h ON a.sole_id = h.id");
        sql.append(" JOIN color i ON a.color_id = i.id where 1 = 1 and d.id = :idShoe GROUP BY a.id, d.name");
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("idShoe", idShoe);
        List<Object[]> results = query.getResultList();
        return results;
    }

    @Override
    public List<Object[]> getListSizeExits(Long idShoe, Long idColor) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT shoe_detail.size_id FROM shoe_detail");
        sql.append("  JOIN shoe ON shoe_detail.shoe_id = shoe.id");
        sql.append(" JOIN color ON shoe_detail.color_id = color.id");
        sql.append(" JOIN size ON shoe_detail.size_id = size.id");
        sql.append(" WHERE 1 = 1 and shoe_detail.shoe_id = :idShoe and shoe_detail.color_id = :idColor");
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("idShoe", idShoe);
        query.setParameter("idColor", idColor);
        List<Object[]> results = query.getResultList();
        return results;
    }
}
