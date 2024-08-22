package com.ex.service;

import org.springframework.stereotype.Service;
import com.ex.repository.FaqRepository;
import com.ex.entity.FaqEntity;
import com.ex.data.FaqDTO;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;

    // 모든 FAQ를 조회하여 DTO 리스트로 반환하는 메서드
    public List<FaqDTO> getAllFaqs() {
        return faqRepository.findAll().stream()
                .map(this::entityToDTO)  // 엔티티를 DTO로 변환
                .collect(Collectors.toList());
    }

    // 특정 ID의 FAQ를 조회하여 DTO로 반환하는 메서드
    public FaqDTO getFaqById(Long id) {
        Optional<FaqEntity> faqEntity = faqRepository.findById(id);
        return faqEntity.map(this::entityToDTO)
                        .orElseThrow(() -> new RuntimeException("FAQ not found"));
    }

    // 새로운 FAQ를 등록하는 메서드
    public void createFaq(FaqDTO faqDTO) {
        FaqEntity faqEntity = FaqEntity.builder()
                .question(faqDTO.getQuestion())
                .answer(faqDTO.getAnswer())
                .build();
        faqRepository.save(faqEntity);
    }

    // FAQ 수정하는 메서드
    public void updateFaq(Long id, FaqDTO faqDTO) {
        FaqEntity faqEntity = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found"));
        
        faqEntity.setQuestion(faqDTO.getQuestion());
        faqEntity.setAnswer(faqDTO.getAnswer());
        faqRepository.save(faqEntity);
    }

    // FAQ를 삭제하는 메서드
    public void deleteFaq(Long id) {
        faqRepository.deleteById(id);
    }

    // 엔티티를 DTO로 변환하는 메서드
    private FaqDTO entityToDTO(FaqEntity faqEntity) {
        return FaqDTO.builder()
                .id(faqEntity.getId())
                .question(faqEntity.getQuestion())
                .answer(faqEntity.getAnswer())
                .build();
    }
}
