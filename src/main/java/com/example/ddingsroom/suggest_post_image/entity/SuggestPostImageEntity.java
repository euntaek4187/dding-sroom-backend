package com.example.ddingsroom.suggest_post_image.entity;

import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "suggest_post_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestPostImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "suggest_post_id", nullable = false)
    private SuggestPostEntity suggestPost;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false, length = 512)
    private String fileUrl;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedDate;

    public SuggestPostImageEntity(SuggestPostEntity suggestPost, String fileName, String fileUrl, String fileType, LocalDateTime uploadedDate) {
        this.suggestPost = suggestPost;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.uploadedDate = uploadedDate;
    }
}
