package com.ex.data;

import java.util.List;

import com.ex.entity.NoticeEntity;
import com.ex.entity.NoticePhotoEntity;
import com.ex.entity.ProductEntity;
import com.ex.entity.ProductThumbnailEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDTO {
    private Integer id;
    private String name; // 상품명
    private String description; // 상품 설명
    
    private List<PhotoDTO> noticePhotoPath; // 썸네일 이미지 경로
    
    public static NoticeDTO entityToDTO(NoticeEntity ne) {
		return  NoticeDTO.builder()
				.id(ne.getId())
				.name(ne.getName())
				.description(ne.getDescription())
				.build();
    }
}
