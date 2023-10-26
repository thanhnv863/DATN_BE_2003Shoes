package com.backend.repository;

import com.backend.entity.Thumbnail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThumbnailRepository extends JpaRepository<Thumbnail, Long> {
    void deleteByShoeDetail_Id(Long shoeDetailId);
}
