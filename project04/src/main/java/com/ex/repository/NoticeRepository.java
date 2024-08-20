package com.ex.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ex.entity.NoticeEntity;
import com.ex.entity.ProductEntity;

public interface NoticeRepository  extends JpaRepository<NoticeEntity, Integer> {
    Page<NoticeEntity> findAll(Specification<NoticeEntity> spec, Pageable pageable);

    NoticeEntity findTopByOrderByIdDesc(); // ID를 기준으로 최신 공지사항을 가져옴
}
