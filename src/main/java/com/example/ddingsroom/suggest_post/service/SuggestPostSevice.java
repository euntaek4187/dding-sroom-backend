package com.example.ddingsroom.suggest_post.service;

import com.example.ddingsroom.suggest_post.dto.SuggestPostDTO;
import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post.repository.SuggestPostRepository;
import com.example.ddingsroom.suggest_post.util.Category;
import com.example.ddingsroom.suggest_post.util.Location;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class SuggestPostSevice {

    private final SuggestPostRepository suggestPostRepository;

    public SuggestPostSevice(SuggestPostRepository suggestPostRepository) {
        this.suggestPostRepository = suggestPostRepository;
    }

    @Transactional
    public SuggestPostEntity createSuggestPost(@Valid SuggestPostDTO request) {

        Category categoryEnum = Category.fromName(request.getCategory());
        if(categoryEnum == null){
            throw new IllegalArgumentException("유효하지 않은 카테고리 값입니다: " + request.getCategory() +
                    ". 유효한 값: " + String.join(", ", Arrays.stream(Category.values()).map(Category::getName).collect(Collectors.toList())));
        }
        int categoryValue = categoryEnum.getValue();

        Location locationEnum = Location.fromName(request.getLocation());
        if(locationEnum == null){
            throw new IllegalArgumentException("유효하지 않은 위치 값입니다: " + request.getLocation() +
                    ". 유효한 값: " + String.join(", ", Arrays.stream(Location.values()).map(Location::getName).collect(Collectors.toList())));
        }
        int locationValue = locationEnum.getValue();

        SuggestPostEntity newPost = new SuggestPostEntity(
                request.getUserId(),
                request.getSuggestTitle(),
                request.getSuggestContent(),
                categoryValue,
                locationValue
        );

        return suggestPostRepository.save(newPost);
    }
}
