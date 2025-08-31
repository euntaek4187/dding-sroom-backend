package com.example.ddingsroom.community_post_comment.repository;

import com.example.ddingsroom.community_post_comment.entity.CommunityPostCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface CommunityPostCommentRepository extends JpaRepository<CommunityPostCommentEntity, Long> {

    // 기존 메서드들 (JPA 관계로 수정)
    @Query("SELECT c FROM CommunityPostCommentEntity c WHERE c.communityPost.id = :postId")
    List<CommunityPostCommentEntity> findByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM CommunityPostCommentEntity c WHERE c.user.id = :userId")
    List<CommunityPostCommentEntity> findByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM CommunityPostCommentEntity c WHERE c.communityPost.id = :postId")
    Long countByPostId(@Param("postId") Long postId);

    // 대댓글 관련 메서드들
    List<CommunityPostCommentEntity> findByParentCommentId(Long parentCommentId);
    Long countByParentCommentId(Long parentCommentId);

    // 유저별 댓글 조회 메서드들 (JPA 관계로 수정)
    @Query("SELECT c FROM CommunityPostCommentEntity c WHERE c.user.id = :userId AND c.parentCommentId IS NULL")
    List<CommunityPostCommentEntity> findByUserIdAndParentCommentIdIsNull(@Param("userId") Long userId);

    @Query("SELECT c FROM CommunityPostCommentEntity c WHERE c.user.id = :userId AND c.parentCommentId IS NOT NULL")
    List<CommunityPostCommentEntity> findByUserIdAndParentCommentIdIsNotNull(@Param("userId") Long userId);

    @Query("SELECT c FROM CommunityPostCommentEntity c WHERE c.user.id = :userId AND c.communityPost.id = :postId")
    List<CommunityPostCommentEntity> findByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("SELECT c FROM CommunityPostCommentEntity c WHERE c.user.id = :userId AND c.communityPost.id = :postId AND c.parentCommentId IS NULL")
    List<CommunityPostCommentEntity> findByUserIdAndPostIdAndParentCommentIdIsNull(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("SELECT c FROM CommunityPostCommentEntity c WHERE c.user.id = :userId AND c.communityPost.id = :postId AND c.parentCommentId IS NOT NULL")
    List<CommunityPostCommentEntity> findByUserIdAndPostIdAndParentCommentIdIsNotNull(@Param("userId") Long userId, @Param("postId") Long postId);

    // 계층형 댓글 조회를 위한 @Query 메서드
    @Query("SELECT c FROM CommunityPostCommentEntity c WHERE c.communityPost.id = :postId " +
            "ORDER BY CASE WHEN c.parentCommentId IS NULL THEN c.id ELSE c.parentCommentId END, " +
            "c.parentCommentId ASC NULLS FIRST, c.createdAt ASC")
    List<CommunityPostCommentEntity> findByPostIdOrderByHierarchy(@Param("postId") Long postId);

    // 사용자별 댓글 최신순 조회
    @Query("SELECT c FROM CommunityPostCommentEntity c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<CommunityPostCommentEntity> findAllByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // 특정 게시글의 모든 댓글 삭제
    @Transactional
    @Query("DELETE FROM CommunityPostCommentEntity c WHERE c.communityPost.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    // 특정 사용자의 모든 댓글 삭제
    @Transactional
    @Query("DELETE FROM CommunityPostCommentEntity c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
