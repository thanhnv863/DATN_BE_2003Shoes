package com.backend.entity;

import com.backend.util.DateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "voucher")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "discount_amount")
    private BigDecimal discountAmount;

    @Column(name = "min_order_value")
    private BigDecimal minBillValue;

    @Column(name = "maximum_reduction_value")
    private BigDecimal maximumReductionValue;

    @Column(name = "start_date")
    //@JsonSerialize(using = DateTimeSerializer.class)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    //@JsonSerialize(using = DateTimeSerializer.class)
    private LocalDateTime endDate;

    @Column(name = "created_time")
    //@JsonSerialize(using = DateTimeSerializer.class)
    private LocalDateTime createDate;

    @Column(name = "updated_time")
    //@JsonSerialize(using = DateTimeSerializer.class)
    private LocalDateTime updateAt;

    @Column(name = "reduce_form")
    private Integer reduceForm;
    // Reduce form=0: Giảm tiền
    // Reduce form=1: Giảm %

    @Column(name = "status")
    private Integer status;
    // Status=0: Chờ kích hoạt
    // Status=1: Đã kích hoạt
    // Status=2: Hết hạn
    // Status=3: Khi xoá đổi sang trạng thái ẩn

    @OneToMany(mappedBy = "voucherOrder")
    @JsonIgnore
    private List<Order> orderList;

}
