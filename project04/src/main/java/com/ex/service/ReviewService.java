package com.ex.service;

import com.ex.entity.ProductEntity;
import com.ex.entity.ReviewEntity;
import com.ex.entity.ReviewImgEntity;
import com.ex.entity.UserEntity;
import com.ex.repository.ProductRepository;
import com.ex.repository.ReviewImgRepository;
import com.ex.repository.ReviewRepository;
import com.ex.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final UserRepository userRepository;

    // 상품 ID로 리뷰 목록 가져오기
    public List<ReviewEntity> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProduct_Id(productId);
    }

    @Transactional
    public ReviewEntity saveReview(Integer id, String content, String username) {
        ProductEntity product = productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Invalid username"));

        ReviewEntity review = ReviewEntity.builder()
            .product(product)
            .content(content)
            .writer(user) // UserEntity 설정
            .writeDate(LocalDateTime.now())
            .build();

        return reviewRepository.save(review); // 리뷰 저장
    }

    @Transactional
    public void saveReviewImages(List<ReviewImgEntity> reviewImages) {
        reviewImgRepository.saveAll(reviewImages); // 리뷰 이미지 저장
    }

    // 리뷰와 이미지를 저장하는 메서드
    @Transactional
    public void addReviewWithImages(Integer productId, String content, String username, MultipartFile[] images) {
        ReviewEntity savedReview = saveReview(productId, content, username); // 리뷰 저장

        // ReviewImgEntity 생성 및 저장
        List<ReviewImgEntity> reviewImages = new ArrayList<>();
        String uploadDir = System.getProperty("user.dir") + "/uploads/";

        // 업로드 디렉토리가 존재하지 않으면 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                try {
                    String originalFilename = image.getOriginalFilename();
                    String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
                    String filePath = uploadDir + storedFilename;
                    image.transferTo(new File(filePath)); // 파일 저장
                    reviewImages.add(ReviewImgEntity.builder()
                        .orgname(originalFilename) // 원본 파일 이름 설정
                        .sysname(storedFilename) // 시스템에 저장된 파일 이름 설정
                        .review(savedReview) // 저장된 리뷰와 연관 설정
                        .build());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        saveReviewImages(reviewImages); // 리뷰 이미지 저장
    }
}
