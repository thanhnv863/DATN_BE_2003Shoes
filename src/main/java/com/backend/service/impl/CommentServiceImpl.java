package com.backend.service.impl;

import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.comment.CommentRequest;
import com.backend.dto.request.comment.SearchCommentRequest;
import com.backend.dto.response.CommentResponse;
import com.backend.dto.response.OrderHistoryReponse;
import com.backend.entity.Account;
import com.backend.entity.Comment;
import com.backend.entity.OrderHistory;
import com.backend.entity.ShoeDetail;
import com.backend.repository.AccountRepository;
import com.backend.repository.CommentCustomRepository;
import com.backend.repository.CommentRepository;
import com.backend.repository.ShoeDetailRepository;
import com.backend.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements ICommentService {
    @Autowired
    private CommentCustomRepository commentCustomRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ShoeDetailRepository shoeDetailRepository;

    public CommentResponse convertPage(Object[] object) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(((BigInteger) object[0]).longValue());
        commentResponse.setNameAccount((String) object[1]);
        commentResponse.setContent((String) object[2]);
        commentResponse.setDate((Date) object[3]);
        return commentResponse;
    }

    @Override
    public Page<CommentResponse> searchComment(SearchCommentRequest commentRequest) {
        Pageable pageable = PageRequest.of(commentRequest.getPage(), commentRequest.getSize());
        Page<Object> objects = commentCustomRepository.doSearch(
                pageable,
                commentRequest.getIdShoeDetail()
        );
        List<CommentResponse> list = new ArrayList<>();
        for (Object object : objects) {
            Object[] result = (Object[]) object;
            CommentResponse commentResponse = convertPage(result);
            list.add(commentResponse);
        }
        return new PageImpl<>(list, pageable, objects.getTotalElements());
    }

    @Override
    public ServiceResultReponse<?> add(CommentRequest commentRequest) {
        if (commentRequest.getIdAccount() == null || commentRequest.getIdShoeDetail() == null
                || commentRequest.getContent() == null || commentRequest.getContent().trim().isEmpty()) {
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Không được để trống");
        } else {
            Date date = new Date();
            Comment comment = new Comment();
            Optional<Account> accountCheck = accountRepository.findById(commentRequest.getIdAccount());
            Optional<ShoeDetail> shoeDetailCheck = shoeDetailRepository.findById(commentRequest.getIdShoeDetail());
            if (accountCheck.isPresent() && shoeDetailCheck.isPresent()) {
                Account account = accountCheck.get();
                ShoeDetail shoeDetail = shoeDetailCheck.get();
                comment.setAccount(account);
                comment.setShoeDetail(shoeDetail);
                comment.setContent(commentRequest.getContent());
                comment.setDate(date);
                Comment commentAdd = commentRepository.save(comment);
                CommentResponse commentResponse = convertComment(commentAdd);
                return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, commentResponse, "Thêm thành công comment");
            } else {
                return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Thêm thất bại. Không tồn tại tài khoản hoặc giày");
            }
        }
    }

    public CommentResponse convertComment(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .nameAccount(comment.getAccount().getName())
                .content(comment.getContent())
                .date(comment.getDate())
                .build();
    }
}
