package com.example.ddingsroom.community_post_comment.controller;

import com.example.ddingsroom.community_post_comment.dto.BaseResponseDTO;
import com.example.ddingsroom.community_post_comment.dto.CommunityPostCommentRequestDTO;
import com.example.ddingsroom.community_post_comment.service.CommunityPostCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community-posts/comments")
public class CommunityPostCommentController {

    private final CommunityPostCommentService service;

    @Autowired
    public CommunityPostCommentController(CommunityPostCommentService service) {
        this.service = service;
    }

    // 기존 메서드들...
    @PostMapping
    public ResponseEntity<BaseResponseDTO> createComment(@RequestBody CommunityPostCommentRequestDTO dto) {
        BaseResponseDTO response = service.createComment(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<BaseResponseDTO> updateComment(@RequestBody CommunityPostCommentRequestDTO dto) {
        BaseResponseDTO response = service.updateComment(dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<BaseResponseDTO> deleteComment(@RequestBody CommunityPostCommentRequestDTO dto) {
        BaseResponseDTO response = service.deleteComment(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<BaseResponseDTO> getCommentsByPostId(@PathVariable Long postId) {
        BaseResponseDTO response = service.getCommentsByPostId(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<BaseResponseDTO> getRepliesByCommentId(@PathVariable Long commentId) {
        BaseResponseDTO response = service.getRepliesByCommentId(commentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<BaseResponseDTO> getComment(@PathVariable Long commentId) {
        BaseResponseDTO response = service.getComment(commentId);
        return ResponseEntity.ok(response);
    }

    // ===== 새로 추가할 유저 관련 엔드포인트들 =====

    // 특정 사용자의 모든 댓글 조회 (댓글 + 대댓글)
    @GetMapping("/user/{userId}")
    public ResponseEntity<BaseResponseDTO> getCommentsByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) String type,           // "comment", "reply"
            @RequestParam(required = false) Long postId           // 특정 게시글 필터
    ) {
        BaseResponseDTO response = service.getCommentsByUserId(userId, type, postId);
        return ResponseEntity.ok(response);
    }

    // 특정 사용자의 댓글만 조회
    @GetMapping("/user/{userId}/comments")
    public ResponseEntity<BaseResponseDTO> getOnlyCommentsByUserId(@PathVariable Long userId) {
        BaseResponseDTO response = service.getOnlyCommentsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    // 특정 사용자의 대댓글만 조회
    @GetMapping("/user/{userId}/replies")
    public ResponseEntity<BaseResponseDTO> getOnlyRepliesByUserId(@PathVariable Long userId) {
        BaseResponseDTO response = service.getOnlyRepliesByUserId(userId);
        return ResponseEntity.ok(response);
    }

    // 특정 사용자가 특정 게시글에 단 댓글들
    @GetMapping("/user/{userId}/post/{postId}")
    public ResponseEntity<BaseResponseDTO> getCommentsByUserIdAndPostId(
            @PathVariable Long userId,
            @PathVariable Long postId
    ) {
        BaseResponseDTO response = service.getCommentsByUserIdAndPostId(userId, postId);
        return ResponseEntity.ok(response);
    }
}
