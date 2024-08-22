package com.ex.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class NoticeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	 private String name; // 상품명
	 private String description; // 상품 설명
	 
	 @OneToMany(mappedBy = "notice", cascade = CascadeType.REMOVE)
	 @JsonIgnore // 직렬화에서 제외
	 private List<NoticePhotoEntity> noticePhoto; //썸네일
	 
	 private int main;
	    
}
