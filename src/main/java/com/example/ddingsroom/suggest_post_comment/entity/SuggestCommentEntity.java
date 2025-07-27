package com.example.ddingsroom.suggest_post_comment.entity;

import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "suggest_comment")
public class SuggestCommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suggest_post_id", nullable = false, unique = true)
    private SuggestPostEntity suggestPost;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "answer_content", nullable = false, columnDefinition = "TEXT")
    private String answerContent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public SuggestCommentEntity() {}

    public SuggestCommentEntity(SuggestPostEntity suggestPost, Long userId, String answerContent) {
        this.suggestPost = suggestPost;
        this.userId = userId;
        this.answerContent = answerContent;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public SuggestPostEntity getSuggestPost(){
        return suggestPost;
    }

    public void setSuggestPost(SuggestPostEntity suggestPost){
        this.suggestPost = suggestPost;
    }

    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId = userId;
    }

    public String getAnswerContent(){
        return answerContent;
    }

    public void setAnswerContent(String answerContent){
        this.answerContent = answerContent;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt){
        this.updatedAt = updatedAt;
    }

}
