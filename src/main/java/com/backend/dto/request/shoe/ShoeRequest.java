package com.backend.dto.request.shoe;

import com.backend.entity.ShoeDetail;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShoeRequest {
    private String name;

    private Date createdAt;

    private Date updatedAt;

    private Integer statusShoe;

    private List<ShoeDetail> shoeDetailList;

//    private String thumbnail;
//
//    private List<String> imageList;
}
