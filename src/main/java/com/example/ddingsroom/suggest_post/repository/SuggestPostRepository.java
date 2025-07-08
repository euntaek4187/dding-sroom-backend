package com.example.ddingsroom.suggest_post.repository;

import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestPostRepository extends JpaRepository<SuggestPostEntity, Long>, JpaSpecificationExecutor<SuggestPostEntity> {

}
