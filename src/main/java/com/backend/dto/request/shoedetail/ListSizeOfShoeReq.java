package com.backend.dto.request.shoedetail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ListSizeOfShoeReq {
    private Long idShoe;
    private Long idColor;
}
