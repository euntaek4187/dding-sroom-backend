package com.example.ddingsroom.community_post.service;

import com.example.ddingsroom.community_post.dto.BaseResponseDTO;
import com.example.ddingsroom.community_post.dto.CommunityPostRequestDTO;
import com.example.ddingsroom.community_post.dto.CommunityPostResponseDTO;
import com.example.ddingsroom.community_post.entity.CommunityPostEntity;
import com.example.ddingsroom.community_post.repository.CommunityPostRepository;
import com.example.ddingsroom.CommunityPostComment.repository.CommunityPostCommentRepository;
import com.example.ddingsroom.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommunityPostService {

    private final CommunityPostRepository repository;
    private final CommunityPostCommentRepository commentRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommunityPostService(CommunityPostRepository repository, 
                              CommunityPostCommentRepository commentRepository,
                              UserRepository userRepository) {
        this.repository = repository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    // 게시글 생성
    public BaseResponseDTO createCommunityPost(CommunityPostRequestDTO dto) {
        try {
            // 사용자 존재 확인
            if (!userRepository.existsById(dto.getUserId().intValue())) {
                return BaseResponseDTO.error("존재하지 않는 사용자입니다.");
            }

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

    // 게시글 삭제 (댓글도 함께 삭제)
    @Transactional
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

            // 먼저 댓글들을 삭제
            commentRepository.deleteByPostId(dto.getPostId());
            // 그 다음 게시글 삭제
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

    // CommunityPostService.java에 추가

    // 특정 사용자의 모든 게시글 조회
    public BaseResponseDTO getPostsByUserId(Long userId) {
        try {
            List<CommunityPostEntity> posts = repository.findByUserId(userId);
            List<CommunityPostResponseDTO> responseDTOs = posts.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            String message = String.format("사용자 %d의 게시글 목록 조회 성공 (총 %d개)", userId, responseDTOs.size());
            return BaseResponseDTO.success(message, responseDTOs);
        } catch (Exception e) {
            return BaseResponseDTO.error("사용자 게시글 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 사용자의 특정 카테고리 게시글 조회
    public BaseResponseDTO getPostsByUserIdAndCategory(Long userId, Integer category) {
        try {
            List<CommunityPostEntity> posts = repository.findByUserIdAndCategory(userId, category);
            List<CommunityPostResponseDTO> responseDTOs = posts.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            String message = String.format("사용자 %d의 카테고리 %d 게시글 조회 성공 (총 %d개)",
                    userId, category, responseDTOs.size());
            return BaseResponseDTO.success(message, responseDTOs);
        } catch (Exception e) {
            return BaseResponseDTO.error("사용자별 카테고리 게시글 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 사용자의 게시글 조회 (필터링 옵션)
    public BaseResponseDTO getPostsByUserIdWithFilter(Long userId, Integer category) {
        try {
            List<CommunityPostEntity> posts;

            if (category != null) {
                posts = repository.findByUserIdAndCategory(userId, category);
            } else {
                posts = repository.findByUserId(userId);
            }

            List<CommunityPostResponseDTO> responseDTOs = posts.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            String message = category != null ?
                    String.format("사용자 %d의 카테고리 %d 게시글 조회 성공 (총 %d개)", userId, category, responseDTOs.size()) :
                    String.format("사용자 %d의 전체 게시글 조회 성공 (총 %d개)", userId, responseDTOs.size());

            return BaseResponseDTO.success(message, responseDTOs);
        } catch (Exception e) {
            return BaseResponseDTO.error("사용자 게시글 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 사용자의 모든 게시글과 댓글 삭제 (회원 탈퇴 시 사용)
    @Transactional
    public void deleteAllUserPosts(Long userId) {
        try {
            // 사용자의 모든 게시글 ID 조회
            List<CommunityPostEntity> userPosts = repository.findByUserId(userId);
            
            // 각 게시글의 댓글들 삭제
            for (CommunityPostEntity post : userPosts) {
                commentRepository.deleteByPostId(post.getId());
            }
            
            // 사용자의 모든 게시글 삭제
            repository.deleteByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("사용자 게시글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

}
