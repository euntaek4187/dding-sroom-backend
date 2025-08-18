package com.example.ddingsroom.suggest_post_comment.controller;

import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.repository.UserRepository;
import com.example.ddingsroom.suggest_post_comment.dto.*;
import com.example.ddingsroom.suggest_post_comment.service.SuggestCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class SuggestCommentController {

    private final SuggestCommentService suggestCommentService;
    private final UserRepository userRepository;

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return true;
            }
        }
        return false;
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new SecurityException("로그인되지 않은 사용자입니다.");
        }

        Object principal = authentication.getPrincipal();

        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            throw new IllegalStateException("인증된 사용자 principal의 타입을 알 수 없습니다: " + principal.getClass().getName());
        }

        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new IllegalStateException("인증된 사용자(" + username + ")의 정보를 데이터베이스에서 찾을 수 없습니다.");
        }
        return (long) userEntity.getId();
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createSuggestComment(@Valid @RequestBody SuggestCommentDTO request) {
        Map<String, String> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!isAdmin(authentication)) {
                response.put("error", "관리자만 댓글을 생성할 수 있습니다.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            suggestCommentService.createComment(request, request.getUserId());
            response.put("message", "건의 댓글이 성공적으로 생성되었습니다!");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (SecurityException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("error", "건의 댓글 생성 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> updateSuggestComment(@Valid @RequestBody SuggestCommentUpdateRequestDTO request) {
        Map<String, String> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!isAdmin(authentication)) {
                response.put("error", "관리자만 댓글을 업데이트할 수 있습니다.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            suggestCommentService.updateComment(request);
            response.put("message", "건의 댓글이 성공적으로 업데이트되었습니다!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("error", "건의 댓글 업데이트 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteSuggestComment(@Valid @RequestBody SuggestCommentDeleteRequestDTO request) {
        Map<String, String> response = new HashMap<>();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!isAdmin(authentication)) {
                response.put("error", "관리자만 댓글을 삭제할 수 있습니다.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            suggestCommentService.deleteComment(request);
            response.put("message", "건의 댓글이 성공적으로 삭제되었습니다!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
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
            List<SuggestCommentResponseDTO> comments = suggestCommentService.getSuggestCommentsByPostId(postId);

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