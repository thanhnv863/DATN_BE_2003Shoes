package com.backend.dto.request;

import com.backend.entity.Brand;
import com.backend.entity.Category;
import com.backend.entity.Color;
import com.backend.entity.Image;
import com.backend.entity.Shoe;
import com.backend.entity.Size;
import com.backend.entity.Sole;
import com.backend.entity.Thumbnail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ShoeDetailRequestUpdate {
    private Long id;

    private Shoe shoe;

    private Category category;

    private Size size;

    private Color color;

    private Sole sole;

    private Brand brand;

    private String code;

    private String qrCode;

    private BigDecimal priceInput;

    private Integer quantity;

    private String createdBy;

    private String updatedBy;

    private Date createdAt;

    private Date updatedAt;

    private Integer status;

    private List<Thumbnail> thumbnails;

    private List<Image> images;
}
