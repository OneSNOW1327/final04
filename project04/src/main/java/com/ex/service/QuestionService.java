package com.ex.service;

import com.ex.data.QuestionDTO;
import com.ex.entity.QuestionEntity;
import com.ex.entity.UserEntity;
import com.ex.repository.QuestionRepository;
import com.ex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    // 사용자별 문의 목록 가져오기
    public List<QuestionDTO> findByUsername(String username) {
        return questionRepository.findByUser_Username(username).stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    // 모든 문의 가져오기
    public List<QuestionDTO> findAll() {
        return questionRepository.findAll().stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    // 문의 저장하기
    public void save(QuestionDTO questionDTO, String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        QuestionEntity questionEntity = dtoToEntity(questionDTO, user);
        questionRepository.save(questionEntity);
    }

    // 문의 ID로 삭제하기
    public void deleteById(Long id, String username) {
        QuestionEntity question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));
        if (!question.getUser().getUsername().equals(username)) {
            throw new RuntimeException("자신이 작성한 문의만 삭제할 수 있습니다.");
        }
        questionRepository.deleteById(id);
    }
    
    // 문의 ID로 찾기
    public QuestionDTO findById(Long id) {
        QuestionEntity questionEntity = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));
        return entityToDTO(questionEntity);
    }

    // 문의에 답변 저장하기
    public void saveAnswer(Long id, String answer) {
        QuestionEntity questionEntity = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의가 존재하지 않습니다."));
        questionEntity.setAnswer(answer);
        questionEntity.setAnsweredDate(LocalDateTime.now());
        questionRepository.save(questionEntity);
    }

    // QuestionDTO를 QuestionEntity로 변환
    private QuestionEntity dtoToEntity(QuestionDTO questionDTO, UserEntity user) {
        return QuestionEntity.builder()
                .title(questionDTO.getTitle())
                .content(questionDTO.getContent())
                .user(user)
                .createdDate(LocalDateTime.now())
                .build();
    }

    // QuestionEntity를 QuestionDTO로 변환
    private QuestionDTO entityToDTO(QuestionEntity questionEntity) {
        return QuestionDTO.builder()
                .id(questionEntity.getId())
                .title(questionEntity.getTitle())
                .content(questionEntity.getContent())
                .username(questionEntity.getUser().getUsername())
                .createdDate(questionEntity.getCreatedDate())
                .answer(questionEntity.getAnswer())
                .answeredDate(questionEntity.getAnsweredDate())
                .build();
    }
}
