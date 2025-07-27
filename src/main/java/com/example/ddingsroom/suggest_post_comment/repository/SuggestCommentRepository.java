package com.example.ddingsroom.suggest_post_comment.repository;

import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post_comment.entity.SuggestCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuggestCommentRepository extends JpaRepository<SuggestCommentEntity, Long> {
    List<SuggestCommentEntity> findAllBySuggestPost_Id(Long suggestPostId);
}
