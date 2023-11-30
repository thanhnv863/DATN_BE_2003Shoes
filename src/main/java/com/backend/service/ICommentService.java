package com.backend.service;

import com.backend.ServiceResultReponse;
import com.backend.dto.request.comment.CommentRequest;
import com.backend.dto.request.comment.SearchCommentRequest;
import com.backend.dto.request.paymentMethod.PaymentMethodRequest;
import com.backend.dto.response.CommentResponse;
import com.backend.entity.Comment;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ICommentService {
    Page<CommentResponse> searchComment(SearchCommentRequest commentRequest);

    ServiceResultReponse<?> add(CommentRequest commentRequest);

    ServiceResultReponse<?> checkCommentIsPresent(CommentRequest commentRequest);

    List<CommentResponse> top3();
}
