package com.ex.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaqEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // FAQ 고유 ID

    @Column(nullable = false)
    private String question;  // FAQ 질문 텍스트

    @Column(nullable = false)
    private String answer;  // FAQ 답변 텍스트

    @Column(nullable = false)
    private String writer;  // 작성자 (FAQ 등록자의 이름)

    @Column(nullable = false)
    private int grade;  // 작성자의 등급 (1: 일반 사용자, 3: 관리자)
}
