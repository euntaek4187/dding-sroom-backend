package com.example.ddingsroom.suggest_post_comment.controller;

import com.example.ddingsroom.suggest_post_comment.dto.SuggestCommentDTO;
import com.example.ddingsroom.suggest_post_comment.service.SuggestCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class SuggestCommentController {

    private final SuggestCommentService suggestCommentService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createSuggestComment(@Valid @RequestBody SuggestCommentDTO request) {
        Map<String, String> response = new HashMap<>();
        try {
            suggestCommentService.createComment(request);
            response.put("message", "건의 댓글이 성공적으로 생성되었습니다!");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("error", "건의 댓글 생성 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
