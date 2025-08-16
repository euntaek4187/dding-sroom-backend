package com.example.ddingsroom.community_post.service;

import com.example.ddingsroom.community_post.dto.BaseResponseDTO;
import com.example.ddingsroom.community_post.dto.CommunityPostRequestDTO;
import com.example.ddingsroom.community_post.dto.CommunityPostResponseDTO;
import com.example.ddingsroom.community_post.entity.CommunityPostEntity;
import com.example.ddingsroom.community_post.repository.CommunityPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommunityPostService {

    private final CommunityPostRepository repository;

    @Autowired
    public CommunityPostService(CommunityPostRepository repository) {
        this.repository = repository;
    }

    // 게시글 생성
    public BaseResponseDTO createCommunityPost(CommunityPostRequestDTO dto) {
        try {
            CommunityPostEntity entity = new CommunityPostEntity();
            entity.setUserId(dto.getUserId());
            entity.setTitle(dto.getTitle());
            entity.setContent(dto.getContent());
            entity.setCategory(dto.getCategory());

            CommunityPostEntity savedEntity = repository.save(entity);
            CommunityPostResponseDTO responseDTO = convertToResponseDTO(savedEntity);

            return BaseResponseDTO.success("게시글이 성공적으로 생성되었습니다!", responseDTO);

        } catch (Exception e) {
            return BaseResponseDTO.error("게시글 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 게시글 수정
    public BaseResponseDTO updateCommunityPost(CommunityPostRequestDTO dto) {
        try {
            Optional<CommunityPostEntity> optional = repository.findById(dto.getPostId());
            if (optional.isEmpty()) {
                return BaseResponseDTO.error("게시글을 찾을 수 없습니다.");
            }

            CommunityPostEntity entity = optional.get();
            if (!entity.getUserId().equals(dto.getUserId())) {
                return BaseResponseDTO.error("작성자만 수정할 수 있습니다.");
            }

            entity.setTitle(dto.getTitle());
            entity.setContent(dto.getContent());
            entity.setCategory(dto.getCategory());

            CommunityPostEntity updatedEntity = repository.save(entity);
            CommunityPostResponseDTO responseDTO = convertToResponseDTO(updatedEntity);

            return BaseResponseDTO.success("게시글이 성공적으로 수정되었습니다!", responseDTO);

        } catch (Exception e) {
            return BaseResponseDTO.error("게시글 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 게시글 삭제
    public BaseResponseDTO deleteCommunityPost(CommunityPostRequestDTO dto) {
        try {
            Optional<CommunityPostEntity> optional = repository.findById(dto.getPostId());
            if (optional.isEmpty()) {
                return BaseResponseDTO.error("삭제하려는 게시글이 존재하지 않습니다.");
            }

            CommunityPostEntity entity = optional.get();
            if (!entity.getUserId().equals(dto.getUserId())) {
                return BaseResponseDTO.error("작성자만 삭제할 수 있습니다.");
            }

            repository.deleteById(dto.getPostId());
            return BaseResponseDTO.success("게시글이 성공적으로 삭제되었습니다!");

        } catch (Exception e) {
            return BaseResponseDTO.error("게시글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 조건부 조회 (기존 retrieve 메서드)
    public List<CommunityPostResponseDTO> retrieveCommunityPost(Long postId, Long userId, Integer category) {
        try {
            List<CommunityPostEntity> posts;

            if (postId != null) {
                posts = repository.findById(postId)
                        .map(List::of)
                        .orElse(Collections.emptyList());
            } else if (userId != null && category != null) {
                posts = repository.findByUserIdAndCategory(userId, category);
            } else if (userId != null) {
                posts = repository.findByUserId(userId);
            } else if (category != null) {
                posts = repository.findByCategory(category);
            } else {
                posts = repository.findAll();
            }

            return posts.stream().map(this::convertToResponseDTO).collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // 전체 목록 조회 - 수정됨!
    public BaseResponseDTO getAllCommunityPosts() {
        try {
            List<CommunityPostEntity> posts = repository.findAll();
            List<CommunityPostResponseDTO> responseDTOs = posts.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            return BaseResponseDTO.success("게시글 목록 조회 성공", responseDTOs);
        } catch (Exception e) {
            return BaseResponseDTO.error("게시글 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 게시글 조회 - 수정됨!
    public BaseResponseDTO getCommunityPost(Long id) {
        try {
            Optional<CommunityPostEntity> optional = repository.findById(id);
            if (optional.isPresent()) {
                CommunityPostResponseDTO responseDTO = convertToResponseDTO(optional.get());
                return BaseResponseDTO.success("게시글 조회 성공", responseDTO);
            } else {
                return BaseResponseDTO.error("해당 게시글을 찾을 수 없습니다. ID: " + id);
            }
        } catch (Exception e) {
            return BaseResponseDTO.error("게시글 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // Entity를 ResponseDTO로 변환
    private CommunityPostResponseDTO convertToResponseDTO(CommunityPostEntity entity) {
        CommunityPostResponseDTO dto = new CommunityPostResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setCategory(entity.getCategory());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setMessage("성공");
        return dto;
    }
}
