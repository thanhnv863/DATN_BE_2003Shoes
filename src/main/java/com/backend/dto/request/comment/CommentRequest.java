package com.backend.dto.request.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommentRequest {
    private Long idOrder;
    private Long idAccount;
    private Long idShoeDetail;
    private Integer stars;
    private String content;
}
