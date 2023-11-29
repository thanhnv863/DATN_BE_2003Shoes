package com.backend.dto.response.shoedetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListSizeOfShoe {
    private Float nameOfSize;
    private BigInteger shoeId;
    private String code;
    private BigInteger shoeDetailId;
}
