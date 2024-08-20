package com.ex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ex.entity.NoticePhotoEntity;
import com.ex.entity.ProductThumbnailEntity;

@Repository
public interface NoticePhotoRepository extends JpaRepository<NoticePhotoEntity, Integer> {

	List<NoticePhotoEntity> findByNoticeIdOrderByIdAsc(Integer noticeId);

}
