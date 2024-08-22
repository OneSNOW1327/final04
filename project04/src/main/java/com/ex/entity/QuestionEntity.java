package com.ex.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 문의 ID

    private String title; // 문의 제목
    
    private String content; // 문의 내용

    @ManyToOne
    private UserEntity user; // 작성자

    private LocalDateTime createdDate; // 작성 날짜

    private String answer; // 답변 내용

    private LocalDateTime answeredDate; // 답변 작성 날짜
}
