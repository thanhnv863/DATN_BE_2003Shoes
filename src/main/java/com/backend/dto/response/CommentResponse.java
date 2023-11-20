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
public class CommentResponse {
    private Long id;
    private String nameAccount;
//    private String nameShoeDetail;
    private Integer start;
    private String content;

    @JsonSerialize(using = DateTimeSerializer.class)
    private Date date;
}
