package com.example.ddingsroom.suggest_post_comment.entity;

import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "suggest_comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public SuggestCommentEntity(SuggestPostEntity suggestPost, Long userId, String answerContent) {
        this.suggestPost = suggestPost;
        this.userId = userId;
        this.answerContent = answerContent;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}
