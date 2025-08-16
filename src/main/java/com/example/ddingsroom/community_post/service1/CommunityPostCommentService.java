package com.example.ddingsroom.community_post.service1;

import com.example.ddingsroom.community_post.dto1.BaseResponseDTO;
import com.example.ddingsroom.community_post.dto1.CommunityPostCommentRequestDTO;
import com.example.ddingsroom.community_post.dto1.CommunityPostCommentResponseDTO;
import com.example.ddingsroom.community_post.entity1.CommunityPostCommentEntity;
import com.example.ddingsroom.community_post.repository1.CommunityPostCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommunityPostCommentService {

    private final CommunityPostCommentRepository repository;

    @Autowired
    public CommunityPostCommentService(CommunityPostCommentRepository repository) {
        this.repository = repository;
    }

    public BaseResponseDTO createComment(CommunityPostCommentRequestDTO dto) {
        try {
            CommunityPostCommentEntity entity = new CommunityPostCommentEntity();
            entity.setPostId(dto.getPostId());
            entity.setUserId(dto.getUserId());
            entity.setCommentContent(dto.getCommentContent());

            CommunityPostCommentEntity savedEntity = repository.save(entity);
            CommunityPostCommentResponseDTO responseDTO = convertToResponseDTO(savedEntity);

            return BaseResponseDTO.success("댓글이 성공적으로 생성되었습니다!", responseDTO);

        } catch (Exception e) {
            return BaseResponseDTO.error("댓글 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public BaseResponseDTO updateComment(CommunityPostCommentRequestDTO dto) {
        try {
            Optional<CommunityPostCommentEntity> optional = repository.findById(dto.getCommentId());
            if (optional.isEmpty()) {
                return BaseResponseDTO.error("댓글을 찾을 수 없습니다.");
            }

            CommunityPostCommentEntity entity = optional.get();
            if (!entity.getUserId().equals(dto.getUserId())) {
                return BaseResponseDTO.error("작성자만 수정할 수 있습니다.");
            }

            entity.setCommentContent(dto.getCommentContent());
            CommunityPostCommentEntity updatedEntity = repository.save(entity);
            CommunityPostCommentResponseDTO responseDTO = convertToResponseDTO(updatedEntity);

            return BaseResponseDTO.success("댓글이 성공적으로 수정되었습니다!", responseDTO);

        } catch (Exception e) {
            return BaseResponseDTO.error("댓글 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public BaseResponseDTO deleteComment(CommunityPostCommentRequestDTO dto) {
        try {
            Optional<CommunityPostCommentEntity> optional = repository.findById(dto.getCommentId());
            if (optional.isEmpty()) {
                return BaseResponseDTO.error("삭제하려는 댓글이 존재하지 않습니다.");
            }

            CommunityPostCommentEntity entity = optional.get();
            if (!entity.getUserId().equals(dto.getUserId())) {
                return BaseResponseDTO.error("작성자만 삭제할 수 있습니다.");
            }

            repository.deleteById(dto.getCommentId());
            return BaseResponseDTO.success("댓글이 성공적으로 삭제되었습니다!");

        } catch (Exception e) {
            return BaseResponseDTO.error("댓글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public BaseResponseDTO getCommentsByPostId(Long postId) {
        try {
            List<CommunityPostCommentEntity> comments = repository.findByPostId(postId);
            List<CommunityPostCommentResponseDTO> responseDTOs = comments.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            return BaseResponseDTO.success("댓글 목록 조회 성공", responseDTOs);
        } catch (Exception e) {
            return BaseResponseDTO.error("댓글 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public BaseResponseDTO getComment(Long commentId) {
        try {
            Optional<CommunityPostCommentEntity> optional = repository.findById(commentId);
            if (optional.isPresent()) {
                CommunityPostCommentResponseDTO responseDTO = convertToResponseDTO(optional.get());
                return BaseResponseDTO.success("댓글 조회 성공", responseDTO);
            } else {
                return BaseResponseDTO.error("해당 댓글을 찾을 수 없습니다. ID: " + commentId);
            }
        } catch (Exception e) {
            return BaseResponseDTO.error("댓글 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    private CommunityPostCommentResponseDTO convertToResponseDTO(CommunityPostCommentEntity entity) {
        CommunityPostCommentResponseDTO dto = new CommunityPostCommentResponseDTO();
        dto.setId(entity.getId());
        dto.setPostId(entity.getPostId());
        dto.setUserId(entity.getUserId());
        dto.setCommentContent(entity.getCommentContent());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setMessage("성공");
        return dto;
    }
}
