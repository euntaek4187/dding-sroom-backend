package com.example.ddingsroom.community_post.controller;

import com.example.ddingsroom.community_post.dto.BaseResponseDTO;
import com.example.ddingsroom.community_post.dto.CommunityPostRequestDTO;
import com.example.ddingsroom.community_post.dto.CommunityPostResponseDTO;
import com.example.ddingsroom.community_post.service.CommunityPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community-posts")
public class CommunityPostController {

    private final CommunityPostService service;

    @Autowired
    public CommunityPostController(CommunityPostService service) {
        this.service = service;
    }

    // 게시글 생성
    @PostMapping
    public ResponseEntity<BaseResponseDTO> createCommunityPost(@RequestBody CommunityPostRequestDTO dto) {
        BaseResponseDTO response = service.createCommunityPost(dto);
        return ResponseEntity.ok(response);
    }

    // 게시글 수정
    @PutMapping
    public ResponseEntity<BaseResponseDTO> updateCommunityPost(@RequestBody CommunityPostRequestDTO dto) {
        BaseResponseDTO response = service.updateCommunityPost(dto);
        return ResponseEntity.ok(response);
    }

    // 게시글 삭제
    @DeleteMapping
    public ResponseEntity<BaseResponseDTO> deleteCommunityPost(@RequestBody CommunityPostRequestDTO dto) {
        BaseResponseDTO response = service.deleteCommunityPost(dto);
        return ResponseEntity.ok(response);
    }

    // 조건부 조회 (기존 방식 유지)
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> retrieveCommunityPost(
            @RequestParam(required = false) Long post_id,
            @RequestParam(required = false) Long user_id,
            @RequestParam(required = false) Integer category
    ) {
        List<CommunityPostResponseDTO> posts = service.retrieveCommunityPost(post_id, user_id, category);
        return ResponseEntity.ok(Map.of("posts", posts));
    }

    // 전체 목록 조회
    @GetMapping
    public ResponseEntity<BaseResponseDTO> getAllCommunityPosts() {
        BaseResponseDTO response = service.getAllCommunityPosts();
        return ResponseEntity.ok(response);
    }

    // 특정 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponseDTO> getCommunityPost(@PathVariable Long id) {
        BaseResponseDTO response = service.getCommunityPost(id);
        return ResponseEntity.ok(response);
    }
}
