package com.example.ddingsroom.suggest_post_image.dto;

import com.example.ddingsroom.suggest_post_image.entity.SuggestPostImageEntity;
import com.example.ddingsroom.suggest_post_image.service.SuggestPostImageService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuggestPostImageResponseDTO {
    private Long id;
    @JsonProperty("suggest_post_id")
    private Long suggestPostId;
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("file_url")
    private String fileUrl;
    @JsonProperty("file_type")
    private String fileType;
    @JsonProperty("uploaded_date")
    private LocalDateTime uploadedDate;

    public SuggestPostImageResponseDTO(SuggestPostImageEntity entity){
        this.id = entity.getId();
        this.suggestPostId = entity.getSuggestPost().getId();
        this.fileName = entity.getFileName();
        this.fileUrl = entity.getFileUrl();
        this.fileType = entity.getFileType();
        this.uploadedDate = entity.getUploadedDate();
    }
}
