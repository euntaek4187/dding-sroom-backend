package com.example.ddingsroom.suggest_post_comment.repository;

import com.example.ddingsroom.suggest_post_comment.entity.SuggestPostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestPostCommentRepository extends JpaRepository<SuggestPostCommentEntity, Long> {
    List<SuggestPostCommentEntity> findAllBySuggestPost_Id(Long suggestPostId);
}
