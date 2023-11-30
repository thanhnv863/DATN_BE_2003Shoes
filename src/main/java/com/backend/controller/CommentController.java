package com.backend.controller;

import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.comment.CommentRequest;
import com.backend.dto.request.comment.SearchCommentRequest;
import com.backend.dto.response.CommentResponse;
import com.backend.entity.Comment;
import com.backend.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comment")
public class CommentController {
    @Autowired
    private ICommentService iCommentService;

    @PostMapping("/get-all")
    public ResponseEntity<?> getAllComment(@RequestBody SearchCommentRequest searchCommentRequest) {
        Page<CommentResponse> page = iCommentService.searchComment(searchCommentRequest);
        return ResponseEntity.ok().body(new ServiceResultReponse<>(AppConstant.SUCCESS, page.getTotalElements(), page.getContent(), "Lấy danh sách thành công "));
//        return ResponseEntity.ok().body(page);
    }

    @PostMapping("/save")
    public ResponseEntity<?> addComment(@RequestBody CommentRequest commentRequest) {
        return ResponseEntity.ok().body(iCommentService.add(commentRequest));
    }

    @PostMapping("/get-one")
    public ResponseEntity<?> getOneComment(@RequestBody CommentRequest commentRequest) {
        return ResponseEntity.ok().body(iCommentService.checkCommentIsPresent(commentRequest));
    }

    @GetMapping("/top-3")
    public ResponseEntity<?> top3(){
        return ResponseEntity.ok().body(iCommentService.top3());
    }
}
