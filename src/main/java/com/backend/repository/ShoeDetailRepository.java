package com.backend.repository;

import com.backend.entity.ShoeDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShoeDetailRepository extends JpaRepository<ShoeDetail, Long> {
    @Query(value = "select sd.Id, s.Name as ShoeName, size.Name  as SizeName, category.Name  as CategoryName,\n" +
            "brand.Name  as BrandName, sole.Name  as SoleName, color.Name  as ColorName,\n" +
            "sd.Code , sd.PriceInput,sd.Quantity, thumbnail.ImgUrl as thumbnail\n" +
            "from Shoe s \n" +
            "join ShoeDetail sd on s.Id = sd.ShoeId \n" +
            "join Category category on category.Id = sd.CategoryId\n" +
            "join Brand brand on brand.Id = sd.BrandId\n" +
            "join Sole sole on sole.Id = sd.SoleId\n" +
            "join Color color on color.Id = sd.ColorId\n" +
            "join Size size on size.Id = sd.SizeId\n" +
            "join Thumbnail thumbnail on thumbnail.ShoeDetailId = sd.Id", nativeQuery = true)
    Page<Tuple> getAllShoeDetail(Pageable pageable);

    Page<ShoeDetail> findByShoe_NameContainingAndSize_NameAndBrand_NameContaining(String nameShoe,Float sizeShoe,
                                                                                String brandShoe, Pageable pageable);

    Page<ShoeDetail> findByShoe_NameContainingAndSize_Name(String nameShoe,Float sizeShoe, Pageable pageable);

    Page<ShoeDetail> findByShoe_NameContainingAndBrand_NameContaining(String nameShoe, String brandShoe, Pageable pageable);

    Page<ShoeDetail> findBySize_NameAndBrand_NameContaining(Float sizeShoe, String brandShoe, Pageable pageable);

    Page<ShoeDetail> findByShoe_NameContaining(String nameShoe, Pageable pageable);

    Page<ShoeDetail> findBySize_Name(Float sizeShoe, Pageable pageable);

    Page<ShoeDetail> findByBrand_NameContaining(String brandShoe, Pageable pageable);

    @Query(value = "SELECT * FROM shoe_detail where \n" +
            "shoe_detail.shoe_id = ?1 \n" +
            "and shoe_detail.size_id = ?2 \n" +
            "and shoe_detail.sole_id = ?3 \n" +
            "and shoe_detail.brand_id = ?4 \n" +
            "and shoe_detail.category_id = ?5 \n" +
            "and shoe_detail.color_id = ?6 \n" , nativeQuery = true)
    ShoeDetail getOneByAllForeignkey(Long idShoe, Long idSize, Long idSole, Long idBrand, Long idCategory, Long idColor);

    @Transactional
    @Modifying
    @Query("update ShoeDetail shoeDetail set shoeDetail.quantity = :quantityNew where shoeDetail.id = :idShoeDetail")
    void updateSoLuong(@Param("quantityNew") Integer quantityNew, @Param("idShoeDetail") Long idShoeDetail);
}
