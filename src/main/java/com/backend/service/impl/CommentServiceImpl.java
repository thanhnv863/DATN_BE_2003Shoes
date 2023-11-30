package com.backend.service.impl;

import com.backend.ServiceResultReponse;
import com.backend.config.AppConstant;
import com.backend.dto.request.comment.CommentRequest;
import com.backend.dto.request.comment.SearchCommentRequest;
import com.backend.dto.response.CommentResponse;
import com.backend.dto.response.OrderHistoryReponse;
import com.backend.entity.Account;
import com.backend.entity.Comment;
import com.backend.entity.Order;
import com.backend.entity.OrderHistory;
import com.backend.entity.ShoeDetail;
import com.backend.repository.AccountRepository;
import com.backend.repository.CommentCustomRepository;
import com.backend.repository.CommentRepository;
import com.backend.repository.OrderRepository;
import com.backend.repository.ShoeDetailRepository;
import com.backend.service.ICommentService;
import org.aspectj.weaver.ast.Or;
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


    @Autowired
    private OrderRepository orderRepository;

    public CommentResponse convertPage(Object[] object) {
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(((BigInteger) object[0]).longValue());
        commentResponse.setNameAccount((String) object[1]);
        commentResponse.setStart((Integer) object[2]);
        commentResponse.setContent((String) object[3]);
        commentResponse.setDate((Date) object[4]);
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
        if (commentRequest.getIdAccount() == null || commentRequest.getIdShoeDetail() == null || commentRequest.getIdOrder() == null
                || commentRequest.getContent() == null || commentRequest.getContent().trim().isEmpty()) {
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Không được để trống");
        } else {
            CommentResponse commentResponseCheck = commentRepository.getOne(commentRequest.getIdOrder(), commentRequest.getIdShoeDetail());
            if(commentResponseCheck != null){
                return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Đã tồn tại comment");
            }
            else {
                Date date = new Date();
                Comment comment = new Comment();
                Optional<Account> accountCheck = accountRepository.findById(commentRequest.getIdAccount());
                Optional<ShoeDetail> shoeDetailCheck = shoeDetailRepository.findById(commentRequest.getIdShoeDetail());
                Optional<Order> order = orderRepository.findById(commentRequest.getIdOrder());
                if (accountCheck.isPresent() && shoeDetailCheck.isPresent() && order.isPresent()) {
                    Account account = accountCheck.get();
                    ShoeDetail shoeDetail = shoeDetailCheck.get();
                    Order order1 = order.get();
                    comment.setAccount(account);
                    comment.setShoeDetail(shoeDetail);
                    comment.setOrder(order1);
                    comment.setStars(commentRequest.getStars());
                    comment.setContent(commentRequest.getContent());
                    comment.setDate(date);
                    comment.setStatus(1);
                    Comment commentAdd = commentRepository.save(comment);
                    CommentResponse commentResponse = convertComment(commentAdd);
                    return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, commentResponse, "Thêm thành công comment");
                } else {
                    return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Thêm thất bại. Không tồn tại tài khoản hoặc giày");
                }
            }
        }
    }
    @Override
    public ServiceResultReponse<?> checkCommentIsPresent(CommentRequest commentRequest){
        if (commentRequest.getIdShoeDetail() == null || commentRequest.getIdOrder() == null) {
            return new ServiceResultReponse<>(AppConstant.FAIL, 0L, null, "Không được để trống");
        } else {
            CommentResponse commentResponse = commentRepository.getOne(commentRequest.getIdOrder(), commentRequest.getIdShoeDetail());
            return new ServiceResultReponse<>(AppConstant.SUCCESS, 1L, commentResponse, "Lấy comment thành công");
        }
    }

    @Override
    public List<CommentResponse> top3() {
        List<Object> objectList = commentCustomRepository.top3();
        List<CommentResponse> list = new ArrayList<>();
        for (Object object : objectList) {
            Object[] result = (Object[]) object;
            CommentResponse commentResponse = convertPage(result);
            list.add(commentResponse);
        }
        return list;
    }

    public CommentResponse convertComment(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .nameAccount(comment.getAccount().getName())
                .start(comment.getStars())
                .content(comment.getContent())
                .date(comment.getDate())
                .build();
    }
}
