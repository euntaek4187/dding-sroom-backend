package com.example.ddingsroom.community_post.controller1;

import com.example.ddingsroom.community_post.dto1.BaseResponseDTO;
import com.example.ddingsroom.community_post.dto1.CommunityPostCommentRequestDTO;
import com.example.ddingsroom.community_post.service1.CommunityPostCommentService;
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

    @GetMapping("/{commentId}")
    public ResponseEntity<BaseResponseDTO> getComment(@PathVariable Long commentId) {
        BaseResponseDTO response = service.getComment(commentId);
        return ResponseEntity.ok(response);
    }
}
