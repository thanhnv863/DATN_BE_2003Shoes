package com.backend.dto.request.shoedetail;

import com.backend.entity.ShoeDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShoeDetailRequest {
    private List<ShoeDetail> shoeDetailList;
}
