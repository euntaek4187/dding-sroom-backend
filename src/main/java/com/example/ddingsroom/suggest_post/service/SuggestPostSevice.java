package com.example.ddingsroom.suggest_post.service;

import com.example.ddingsroom.suggest_post.dto.SuggestPostCreateRequestDTO;
import com.example.ddingsroom.suggest_post.dto.SuggestPostDeleteRequestDTO;
import com.example.ddingsroom.suggest_post.dto.SuggestPostResponseDTO;
import com.example.ddingsroom.suggest_post.dto.SuggestPostUpdateRequestDTO;
import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post.repository.SuggestPostRepository;
import com.example.ddingsroom.suggest_post.util.Category;
import com.example.ddingsroom.suggest_post.util.Location;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SuggestPostSevice {

    private final SuggestPostRepository suggestPostRepository;

    public SuggestPostSevice(SuggestPostRepository suggestPostRepository) {
        this.suggestPostRepository = suggestPostRepository;
    }

    private int getCategoryValue(String categoryName) {
        Category categoryEnum = Category.fromName(categoryName);
        if (categoryEnum == null) {
            throw new IllegalArgumentException("유효하지 않은 카테고리 값입니다: '" + categoryName +
                    "'. 유효한 값: " + String.join(", ", Category.getAllNames()));
        }
        return categoryEnum.getValue();
    }

    private int getLocationValue(String locationName) {
        Location locationEnum = Location.fromName(locationName);
        if (locationEnum == null) {
            throw new IllegalArgumentException("유효하지 않은 위치 값입니다: '" + locationName +
                    "'. 유효한 값: " + String.join(", ", Location.getAllNames()));
        }
        return locationEnum.getValue();
    }

    @Transactional
    public SuggestPostEntity createSuggestPost(@Valid SuggestPostCreateRequestDTO request) {

        int categoryValue = getCategoryValue(request.getCategory());
        int locationValue = getLocationValue(request.getLocation());

        SuggestPostEntity newPost = new SuggestPostEntity(
                request.getUserId(),
                request.getSuggestTitle(),
                request.getSuggestContent(),
                categoryValue,
                locationValue
        );

        return suggestPostRepository.save(newPost);
    }

    @Transactional
    public SuggestPostEntity updateSuggestPost(Long suggestId, SuggestPostUpdateRequestDTO request) {
        SuggestPostEntity existingPost = suggestPostRepository.findById(suggestId)
                .orElseThrow(() -> new IllegalArgumentException("건의 게시물을 찾을 수 없습니다. ID: " + suggestId));

        if(!existingPost.getUserId().equals(request.getUserId())){
            throw new IllegalArgumentException("게시물 수정 권한이 없습니다. 사용자 ID: " + request.getUserId());
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
    public void deleteSuggestPost(Long suggestId, SuggestPostDeleteRequestDTO request) {
        SuggestPostEntity existingPost = suggestPostRepository.findById(suggestId)
                .orElseThrow(() -> new IllegalArgumentException("건의 게시물을 찾을 수 없습니다. ID: " + suggestId));

        if(!existingPost.getUserId().equals(request.getUserId())){
            throw new IllegalArgumentException("게시물 삭제 권한이 없습니다. 사용자 ID: " + request.getUserId());
        }

        suggestPostRepository.delete(existingPost);
    }

    @Transactional(readOnly = true)
    public List<SuggestPostResponseDTO> retrieveSuggestPosts(
            Optional<Long> suggestId,
            Optional<Long> userId,
            Optional<String> categoryName) {

        Specification<SuggestPostEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            suggestId.ifPresent(id -> predicates.add(cb.equal(root.get("id"), id)));
            userId.ifPresent(id -> predicates.add(cb.equal(root.get("userId"), id)));

            categoryName.ifPresent(catName -> {
                Category categoryEnum = Category.fromName(catName);
                if (categoryEnum == null) {
                    throw new IllegalArgumentException("유효하지 않은 검색 카테고리 값입니다: '" + catName +
                            "'. 유효한 값: " + String.join(", ", Category.getAllNames()));
                }
                predicates.add(cb.equal(root.get("category"), categoryEnum.getValue()));
            });

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<SuggestPostEntity> entities = suggestPostRepository.findAll(spec);

        return entities.stream()
                .map(SuggestPostResponseDTO::new)
                .collect(Collectors.toList());
    }
}
