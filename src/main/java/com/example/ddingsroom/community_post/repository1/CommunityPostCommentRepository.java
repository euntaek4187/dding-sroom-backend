package com.example.ddingsroom.community_post.repository1;

import com.example.ddingsroom.community_post.entity1.CommunityPostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostCommentRepository extends JpaRepository<CommunityPostCommentEntity, Long> {

    List<CommunityPostCommentEntity> findByPostId(Long postId);
    List<CommunityPostCommentEntity> findByUserId(Long userId);
    Long countByPostId(Long postId);
}
