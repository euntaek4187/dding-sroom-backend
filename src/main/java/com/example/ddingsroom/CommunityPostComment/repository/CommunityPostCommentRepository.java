package com.example.ddingsroom.CommunityPostComment.repository;

import com.example.ddingsroom.CommunityPostComment.entity.CommunityPostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommunityPostCommentRepository extends JpaRepository<CommunityPostCommentEntity, Long> {

    // 기존 메서드들
    List<CommunityPostCommentEntity> findByPostId(Long postId);
    List<CommunityPostCommentEntity> findByUserId(Long userId);
    Long countByPostId(Long postId);

    // 대댓글 관련 메서드들
    List<CommunityPostCommentEntity> findByParentCommentId(Long parentCommentId);
    Long countByParentCommentId(Long parentCommentId);

    // 유저별 댓글 조회 메서드들
    List<CommunityPostCommentEntity> findByUserIdAndParentCommentIdIsNull(Long userId);
    List<CommunityPostCommentEntity> findByUserIdAndParentCommentIdIsNotNull(Long userId);
    List<CommunityPostCommentEntity> findByUserIdAndPostId(Long userId, Long postId);
    List<CommunityPostCommentEntity> findByUserIdAndPostIdAndParentCommentIdIsNull(Long userId, Long postId);
    List<CommunityPostCommentEntity> findByUserIdAndPostIdAndParentCommentIdIsNotNull(Long userId, Long postId);

    // 계층형 댓글 조회를 위한 @Query 메서드
    @Query("SELECT c FROM CommunityPostCommentEntity c WHERE c.postId = :postId " +
            "ORDER BY CASE WHEN c.parentCommentId IS NULL THEN c.id ELSE c.parentCommentId END, " +
            "c.parentCommentId ASC NULLS FIRST, c.createdAt ASC")
    List<CommunityPostCommentEntity> findByPostIdOrderByHierarchy(@Param("postId") Long postId);

    // 사용자별 댓글 최신순 조회
    @Query("SELECT c FROM CommunityPostCommentEntity c WHERE c.userId = :userId ORDER BY c.createdAt DESC")
    List<CommunityPostCommentEntity> findAllByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
