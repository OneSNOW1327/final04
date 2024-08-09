package com.ex.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {

    private Long id; // 문의 ID
    @NotEmpty(message = "제목은 필수 입력 항목입니다.")
    private String title; // 문의 제목
    @NotEmpty(message = "내용은 필수 입력 항목입니다.")
    private String content; // 문의 내용
    private String username; // 작성자
    private LocalDateTime createdDate; // 작성 날짜
    private String answer; // 답변 내용
    private LocalDateTime answeredDate; // 답변 작성 날짜
}
