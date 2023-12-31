package com.backend.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "shoe_detail")
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShoeDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shoe_id")
    private Shoe shoe;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;///

    @ManyToOne
    @JoinColumn(name = "size_id")
    private Size size;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;//

    @ManyToOne
    @JoinColumn(name = "sole_id")
    private Sole sole;///

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;///

    @Column(name = "code")
    private String code;

    @Column(name = "qrcode")
    private String qrCode;

    @Column(name = "price")
    private BigDecimal priceInput;

//    @Column(name = "priceoutput")
//    private BigDecimal priceOutput;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "created_time")
    private Date createdAt;

    @Column(name = "updated_time")
    private Date updatedAt;

    @Column(name = "status")
    private Integer status;

    @OneToMany(mappedBy = "shoeDetail")
    private List<Thumbnail> thumbnails;

    @OneToMany(mappedBy = "shoeDetail")
    private List<Image> images;

}
