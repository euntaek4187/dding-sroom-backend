package com.example.ddingsroom.suggest_post.repository;

import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SuggestPostRepository extends JpaRepository<SuggestPostEntity, Long>, JpaSpecificationExecutor<SuggestPostEntity> {
    
    // 사용자의 모든 건의 게시글 삭제
    @Transactional
    @Query("DELETE FROM SuggestPostEntity s WHERE s.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
    
    // 사용자의 모든 건의 게시글 조회
    @Query("SELECT s FROM SuggestPostEntity s WHERE s.user.id = :userId")
    java.util.List<SuggestPostEntity> findByUserId(@Param("userId") Long userId);
}
