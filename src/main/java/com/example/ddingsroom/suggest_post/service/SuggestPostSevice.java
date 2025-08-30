package com.example.ddingsroom.suggest_post.service;

import com.example.ddingsroom.suggest_post.dto.SuggestPostCreateRequestDTO;
import com.example.ddingsroom.suggest_post.dto.SuggestPostResponseDTO;
import com.example.ddingsroom.suggest_post.dto.SuggestPostUpdateRequestDTO;
import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post.repository.SuggestPostRepository;
import com.example.ddingsroom.suggest_post.util.SuggestPostCategory;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.repository.UserRepository;
import com.example.ddingsroom.suggest_post.util.SuggestPostLocation;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuggestPostSevice {

    private final SuggestPostRepository suggestPostRepository;
    private final UserRepository userRepository;

    private int getCategoryValue(String categoryName) {
        SuggestPostCategory categoryEnum = SuggestPostCategory.fromName(categoryName);
        if (categoryEnum == null) {
            throw new IllegalArgumentException("유효하지 않은 카테고리 값입니다: '" + categoryName +
                    "'. 유효한 값: " + String.join(", ", SuggestPostCategory.getAllNames()));
        }
        return categoryEnum.getValue();
    }

    private int getLocationValue(String locationName) {
        SuggestPostLocation locationEnum = SuggestPostLocation.fromName(locationName);
        if (locationEnum == null) {
            throw new IllegalArgumentException("유효하지 않은 위치 값입니다: '" + locationName +
                    "'. 유효한 값: " + String.join(", ", SuggestPostLocation.getAllNames()));
        }
        return locationEnum.getValue();
    }

    @Transactional
    public SuggestPostEntity createSuggestPost(@Valid SuggestPostCreateRequestDTO request, Long authenticatedUserId) {
        int categoryValue = getCategoryValue(request.getCategory());
        int locationValue = getLocationValue(request.getLocation());

        UserEntity user = userRepository.findById(authenticatedUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + authenticatedUserId));

        SuggestPostEntity newPost = new SuggestPostEntity(
                user,
                request.getSuggestTitle(),
                request.getSuggestContent(),
                categoryValue,
                locationValue
        );
        return suggestPostRepository.save(newPost);
    }

    @Transactional
    public SuggestPostEntity updateSuggestPost(SuggestPostUpdateRequestDTO request, Long authenticatedUserId) {
        SuggestPostEntity existingPost = suggestPostRepository.findById(request.getSuggestId())
                .orElseThrow(() -> new IllegalArgumentException("건의 게시물을 찾을 수 없습니다. ID: " + request.getSuggestId()));

        if (!existingPost.getUserId().equals(authenticatedUserId)) {
            throw new SecurityException("게시물 수정 권한이 없습니다.");
        }

        int  categoryValue = getCategoryValue(request.getCategory());
        int locationValue = getLocationValue(request.getLocation());

        existingPost.setSuggestTitle(request.getSuggestTitle());
        existingPost.setSuggestContent(request.getSuggestContent());
        existingPost.setCategory(categoryValue);
        existingPost.setLocation(locationValue);
        existingPost.setAnswered(request.getIsAnswered());

        return suggestPostRepository.save(existingPost);
    }

    @Transactional
    public void deleteSuggestPost(Long suggestId, Long authenticatedUserId) {
        SuggestPostEntity existingPost = suggestPostRepository.findById(suggestId)
                .orElseThrow(() -> new IllegalArgumentException("건의 게시물을 찾을 수 없습니다. ID: " + suggestId));

        if (!existingPost.getUserId().equals(authenticatedUserId)) {
            throw new SecurityException("게시물 삭제 권한이 없습니다.");
        }

        suggestPostRepository.delete(existingPost);
    }

    @Transactional(readOnly = true)
    public List<SuggestPostResponseDTO> retrieveSuggestPosts(
            Optional<Long> suggestId,
            Optional<Long> userId,
            Optional<String> categoryNameStr,
            Optional<String> locationNameStr,
            Optional<Boolean> isAnswered) {
        Optional<Integer> categoryValueOpt = categoryNameStr.map(this::getCategoryValue);
        Optional<Integer> locationValueOpt = locationNameStr.map(this::getLocationValue);

        Specification<SuggestPostEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            suggestId.ifPresent(id -> predicates.add(cb.equal(root.get("id"), id)));
            userId.ifPresent(id -> predicates.add(cb.equal(root.get("userId"), id)));
            categoryValueOpt.ifPresent(catVal -> predicates.add(cb.equal(root.get("category"), catVal)));
            locationValueOpt.ifPresent(locVal -> predicates.add(cb.equal(root.get("location"), locVal)));
            isAnswered.ifPresent(answered -> predicates.add(cb.equal(root.get("isAnswered"), answered)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<SuggestPostEntity> entities = suggestPostRepository.findAll(spec);
        return entities.stream()
                .map(SuggestPostResponseDTO::new)
                .collect(Collectors.toList());
    }

    // 사용자의 모든 건의 게시글 삭제 (JPA cascade로 댓글 자동 삭제)
    @Transactional
    public void deleteAllUserSuggestPosts(Long userId) {
        try {
            // JPA cascade를 통해 관련된 모든 댓글이 자동으로 삭제됩니다
            suggestPostRepository.deleteByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("사용자 건의 게시글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 사용자의 모든 건의 댓글 삭제 (JPA cascade로 자동 삭제됨)
    @Transactional  
    public void deleteAllUserSuggestComments(Long userId) {
        // 이 메서드는 더 이상 필요하지 않습니다.
        // UserEntity 삭제 시 JPA cascade를 통해 자동으로 삭제됩니다.
    }
}