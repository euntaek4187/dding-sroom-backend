package com.example.ddingsroom.suggest_post.entity;

import com.example.ddingsroom.suggest_post_comment.entity.SuggestPostCommentEntity;
import com.example.ddingsroom.suggest_post_image.entity.SuggestPostImageEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "suggest_post")
public class SuggestPostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String suggestTitle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String suggestContent;

    @Column(nullable = false)
    private int category;

    @Column(nullable = true)
    private int location;

    @Column(nullable = false)
    private boolean isAnswered = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "suggestPost", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SuggestPostCommentEntity comment;

    @OneToMany(mappedBy = "suggestPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SuggestPostImageEntity> images = new ArrayList<>();

    public SuggestPostEntity() {

    }

    public SuggestPostEntity(Long userId, String suggestTitle, String suggestContent, int category, int location) {
        this.userId = userId;
        this.suggestTitle = suggestTitle;
        this.suggestContent = suggestContent;
        this.category = category;
        this.location = location;
        this.isAnswered = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId(){
        return this.id;
    }

    public Long getUserId(){
        return this.userId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public String getSuggestTitle(){
        return this.suggestTitle;
    }

    public void setSuggestTitle(String suggestTitle){
        if (!this.suggestTitle.equals(suggestTitle)) {
            this.suggestTitle = suggestTitle;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public String getSuggestContent(){
        return this.suggestContent;
    }

    public void setSuggestContent(String suggestContent){
        if (!this.suggestContent.equals(suggestContent)) {
            this.suggestContent = suggestContent;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public int getCategory(){
        return this.category;
    }

    public void setCategory(int category){
        if (this.category != category) {
            this.category = category;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public int getLocation(){
        return this.location;
    }

    public void setLocation(int location){
        if (this.location != location) {
            this.location = location;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean isAnswered(){
        return this.isAnswered;
    }

    public void setAnswered(boolean answered){
        this.isAnswered = answered;
    }

    public LocalDateTime getCreatedAt(){
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt(){ return this.updatedAt; }

    public SuggestPostCommentEntity getComment(){
        return comment;
    }

    public void setComment(SuggestPostCommentEntity comment){
        this.comment = comment;
        if (comment != null) {
            comment.setSuggestPost(this);
        }
    }

    public void removeComment(){
        if (this.comment != null){
            this.comment.setSuggestPost(null);
            this.comment = null;
        }
    }

    @Override
    public String toString() {
        return "SuggestPost{"+
                "id=" + id +
                ", userId=" + userId +
                ", suggestTitle=" + suggestTitle +
                ", suggsetContent=" + suggestContent +
                ", category=" + category +
                ", location=" + location +
                ", isAnswered=" + isAnswered +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                "}";
    }
}
