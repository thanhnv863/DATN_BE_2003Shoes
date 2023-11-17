package com.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentCustomRepository {
    Page<Object> doSearch(Pageable pageable,
                          Long idShoeDetail);

    long getTotalCount(Long idShoeDetail);
}
