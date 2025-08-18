package com.example.ddingsroom.suggest_post_comment.controller;

import com.example.ddingsroom.config.SecurityUtils;
import com.example.ddingsroom.suggest_post_comment.dto.*;
import com.example.ddingsroom.suggest_post_comment.service.SuggestPostCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/suggestions/comments")
@RequiredArgsConstructor
public class SuggestPostCommentController {

    private final SuggestPostCommentService suggestPostCommentService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<Map<String, String>> createSuggestComment(@Valid @RequestBody SuggestPostCommentCreateRequestDTO request) {
        Map<String, String> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!securityUtils.isAdmin(authentication)) {
            response.put("error", "댓글 생성 권한이 없습니다. (관리자 전용 기능)");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        try {
            Long authenticatedUserId = securityUtils.getAuthenticatedUserId();
            suggestPostCommentService.createComment(request, authenticatedUserId);
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

    @PutMapping
    public ResponseEntity<Map<String, String>> updateSuggestComment(@Valid @RequestBody SuggestPostCommentUpdateRequestDTO request) {
        Map<String, String> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!securityUtils.isAdmin(authentication)) {
            response.put("error", "댓글 수정 권한이 없습니다. (관리자 전용 기능");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        try {
            Long authenticatedUserId = securityUtils.getAuthenticatedUserId();
            suggestPostCommentService.updateComment(request, authenticatedUserId);
            response.put("message", "건의 댓글이 성공적으로 업데이트되었습니다!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("error", "건의 댓글 업데이트 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteSuggestComment(@Valid @RequestBody SuggestPostCommentDeleteRequestDTO request) {
        Map<String, String> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!securityUtils.isAdmin(authentication)) {
            response.put("error", "댓글 수정 권한이 없습니다. (관리자 전용 기능");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        }

        try {
            Long authenticatedUserId = securityUtils.getAuthenticatedUserId();
            suggestPostCommentService.deleteComment(request, authenticatedUserId);
            response.put("message", "건의 댓글이 성공적으로 삭제되었습니다!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("error", "건의 댓글 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> retrieveSuggestComments(@RequestParam("suggest_post_id") Long postId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SuggestPostCommentResponseDTO> comments = suggestPostCommentService.getSuggestCommentsByPostId(postId);

            if (comments.isEmpty()) {
                response.put("comments", Collections.emptyList());
                response.put("message", "해당 건의에 댓글이 존재하지 않습니다.");
            } else {
                response.put("comments", comments);
                response.put("message", "성공");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("error", "댓글 조회 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}