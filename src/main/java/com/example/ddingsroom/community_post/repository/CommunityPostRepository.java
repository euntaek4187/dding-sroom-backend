package com.example.ddingsroom.community_post.repository;

import com.example.ddingsroom.community_post.entity.CommunityPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPostEntity, Long> {

    List<CommunityPostEntity> findByUserId(Long userId);

    List<CommunityPostEntity> findByCategory(Integer category);

    List<CommunityPostEntity> findByUserIdAndCategory(Long userId, Integer category);
}
