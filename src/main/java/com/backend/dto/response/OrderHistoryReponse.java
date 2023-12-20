package com.backend.dto.response;

import com.backend.util.DateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderHistoryReponse {

//    private Long id;

    private String code;

    @JsonSerialize(using = DateTimeSerializer.class)
    private Date createdTime;

    private String createdBy;

    private String type;

    private String note;
}
