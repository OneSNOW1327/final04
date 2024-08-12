package com.ex.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaqDTO {

    private Long id;  // FAQ 고유 ID

    private String question;  // 질문 텍스트

    private String answer;  // 답변 텍스트

    private String writer;  // 작성자 이름

    private int grade;  // 작성자의 등급
}
