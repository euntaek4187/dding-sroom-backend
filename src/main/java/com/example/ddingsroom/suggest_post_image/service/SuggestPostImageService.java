package com.example.ddingsroom.suggest_post_image.service;

import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post.repository.SuggestPostRepository;
import com.example.ddingsroom.suggest_post_image.dto.SuggestPostImageDeleteRequestDTO;
import com.example.ddingsroom.suggest_post_image.dto.SuggestPostImageResponseDTO;
import com.example.ddingsroom.suggest_post_image.entity.SuggestPostImageEntity;
import com.example.ddingsroom.suggest_post_image.repository.SuggestPostImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SuggestPostImageService {

    private final SuggestPostImageRepository suggestPostImageRepository;
    private final SuggestPostRepository suggestPostRepository;
    private final S3Service s3Service;

    @Transactional
    public SuggestPostImageResponseDTO uploadImage(Long suggestPostId, Long userId, MultipartFile imageFile) throws IOException {
        log.info("uploadImage 메소드 시작: suggestPostId={}, userId={}, fileName={}", suggestPostId, userId, imageFile.getOriginalFilename());

        SuggestPostEntity suggestPost = suggestPostRepository.findById(suggestPostId)
                .orElseThrow(() -> new IllegalArgumentException("건의 게시물을 찾을 수 없습니다. ID: " + suggestPostId));

        if (!suggestPost.getUserId().equals(userId)) {
            log.warn("이미지 업로드 권한 오류: 게시물 작성자 ID {}와 요청자 ID {} 불일치", suggestPost.getUserId(), userId);
            throw new IllegalArgumentException("게시물 작성자만 이미지를 업로드할 수 있습니다.");
        }

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("업로드할 이미지가 비어있습니다.");
        }

        String fileUrl = s3Service.uploadFile(imageFile);
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

        log.info("S3에 파일 업로드 성공: fileUrl={}", fileUrl);

        SuggestPostImageEntity imageEntity = new SuggestPostImageEntity(
                suggestPost,
                fileName,
                fileUrl,
                imageFile.getContentType(),
                LocalDateTime.now()
        );
        SuggestPostImageEntity savedImage = suggestPostImageRepository.save(imageEntity);

        log.info("DB에 이미지 정보 저장 성공: imageId={}", savedImage.getId());

        return new SuggestPostImageResponseDTO(savedImage);
    }

    @Transactional
    public void deleteImage(Long imageId, Long userId) {
        log.info("deleteImage 메소드 시작: imageId={}, userId={}", imageId, userId);

        SuggestPostImageEntity imageEntity = suggestPostImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalArgumentException("이미지를 찾을 수 없습니다. ID: " + imageId));

        SuggestPostEntity suggestPost = imageEntity.getSuggestPost();

        if (!suggestPost.getUserId().equals(userId)) {
            log.warn("이미지 삭제 권한 오류: 게시물 작성자 ID {}와 요청자 ID {} 불일치", suggestPost.getUserId(), userId);
            throw new IllegalArgumentException("게시물 작성자만 이미지를 삭제할 수 있습니다.");
        }

        s3Service.deleteFile(imageEntity.getFileName());
        log.info("S3에서 파일 삭제 성공: fileName={}", imageEntity.getFileName());

        suggestPostImageRepository.delete(imageEntity);
        log.info("DB에서 이미지 정보 삭제 성공: imageId={}", imageId);
    }

    @Transactional(readOnly = true)
    public List<SuggestPostImageResponseDTO> getImagesBySuggestPostId(Long suggestPostId) {
        log.info("getImagesBySuggestPostId 메소드 시작: suggestPostId={}", suggestPostId);

        if (!suggestPostRepository.existsById(suggestPostId)) {
            throw new IllegalArgumentException("건의 게시물을 찾을 수 없습니다. ID: " + suggestPostId);
        }

        List<SuggestPostImageEntity> imageEntities = suggestPostImageRepository.findBySuggestPost_Id(suggestPostId);

        return imageEntities.stream()
                .map(SuggestPostImageResponseDTO::new)
                .collect(Collectors.toList());
    }
}