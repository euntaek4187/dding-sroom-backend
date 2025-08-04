package com.example.ddingsroom.suggest_post_comment.controller;

import com.example.ddingsroom.suggest_post_comment.dto.SuggestCommentDTO;
import com.example.ddingsroom.suggest_post_comment.dto.SuggestCommentUpdateRequestDTO;
import com.example.ddingsroom.suggest_post_comment.service.SuggestCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class SuggestCommentController {

    private final SuggestCommentService suggestCommentService;

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("로그인되지 않은 사용자입니다.");
        }
        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("인증된 사용자 ID를 가져올 수 없습니다. (ID 형식 오류 또눈 UserDetails 구현 확인 필요)");
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createSuggestComment(@Valid @RequestBody SuggestCommentDTO request) {
        Map<String, String> response = new HashMap<>();
        try {
            Long authenticatedUserId = getAuthenticatedUserId();

            suggestCommentService.createComment(request, authenticatedUserId);
            response.put("message", "건의 댓글이 성공적으로 생성되었습니다!");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (SecurityException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("error", "건의 댓글 생성 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> updateSuggestComment(@Valid @RequestBody SuggestCommentUpdateRequestDTO request) {
        Map<String,  String> response = new HashMap<>();
        try{
            Long authenticatedUserId = getAuthenticatedUserId();

            suggestCommentService.updateComment(request,authenticatedUserId);
            response.put("message", "건의 댓글이 성공적으로 업데이트되었습니다!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("error", "건의 댓글 업데이트 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
