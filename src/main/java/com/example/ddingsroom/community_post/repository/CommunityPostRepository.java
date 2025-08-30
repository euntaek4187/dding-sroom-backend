package com.example.ddingsroom.community_post.repository;

import com.example.ddingsroom.community_post.entity.CommunityPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPostEntity, Long> {

    // ========== 사용자 관련 조회 메서드 (user.id로 수정) ==========
    @Query("SELECT p FROM CommunityPostEntity p WHERE p.user.id = :userId")
    List<CommunityPostEntity> findByUserId(@Param("userId") Long userId);

    List<CommunityPostEntity> findByCategory(Integer category);

    @Query("SELECT p FROM CommunityPostEntity p WHERE p.user.id = :userId AND p.category = :category")
    List<CommunityPostEntity> findByUserIdAndCategory(@Param("userId") Long userId, @Param("category") Integer category);

    // ========== 새로 추가할 메서드들 ==========

    // 사용자별 게시글 조회 (최신순)
    @Query("SELECT p FROM CommunityPostEntity p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<CommunityPostEntity> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // 사용자별 특정 카테고리 게시글 조회 (최신순)
    @Query("SELECT p FROM CommunityPostEntity p WHERE p.user.id = :userId AND p.category = :category ORDER BY p.createdAt DESC")
    List<CommunityPostEntity> findByUserIdAndCategoryOrderByCreatedAtDesc(@Param("userId") Long userId, @Param("category") Integer category);

    // 전체 게시글 조회 (최신순)
    @Query("SELECT p FROM CommunityPostEntity p ORDER BY p.createdAt DESC")
    List<CommunityPostEntity> findAllOrderByCreatedAtDesc();

    // 사용자별 게시글 개수 조회
    @Query("SELECT COUNT(p) FROM CommunityPostEntity p WHERE p.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    // 사용자별 특정 카테고리 게시글 개수 조회
    @Query("SELECT COUNT(p) FROM CommunityPostEntity p WHERE p.user.id = :userId AND p.category = :category")
    Long countByUserIdAndCategory(@Param("userId") Long userId, @Param("category") Integer category);

    // 제목으로 검색
    @Query("SELECT p FROM CommunityPostEntity p WHERE p.title LIKE %:title% ORDER BY p.createdAt DESC")
    List<CommunityPostEntity> findByTitleContaining(@Param("title") String title);

    // 특정 사용자의 제목으로 검색
    @Query("SELECT p FROM CommunityPostEntity p WHERE p.user.id = :userId AND p.title LIKE %:title% ORDER BY p.createdAt DESC")
    List<CommunityPostEntity> findByUserIdAndTitleContaining(@Param("userId") Long userId, @Param("title") String title);

    // 사용자의 모든 게시글 삭제
    @Transactional
    @Query("DELETE FROM CommunityPostEntity p WHERE p.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
