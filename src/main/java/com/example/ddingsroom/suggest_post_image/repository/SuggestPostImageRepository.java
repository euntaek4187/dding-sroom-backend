package com.example.ddingsroom.suggest_post_image.repository;

import com.example.ddingsroom.suggest_post_image.entity.SuggestPostImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestPostImageRepository extends JpaRepository<SuggestPostImageEntity, Long> {
    List<SuggestPostImageEntity> findBySuggestPost_Id(Long suggestPostId);
}
