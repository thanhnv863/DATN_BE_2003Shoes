package com.backend.repository;

import com.backend.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByShoeDetailId(Long shoeDetailId);

    void deleteByShoeDetail_Id(Long shoeDetailId);
}
