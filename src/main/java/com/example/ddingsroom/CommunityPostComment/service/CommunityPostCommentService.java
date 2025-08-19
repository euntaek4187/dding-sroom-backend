package com.example.ddingsroom.CommunityPostComment.service;

import com.example.ddingsroom.CommunityPostComment.dto.BaseResponseDTO;
import com.example.ddingsroom.CommunityPostComment.dto.CommunityPostCommentRequestDTO;
import com.example.ddingsroom.CommunityPostComment.dto.CommunityPostCommentResponseDTO;
import com.example.ddingsroom.CommunityPostComment.entity.CommunityPostCommentEntity;
import com.example.ddingsroom.CommunityPostComment.repository.CommunityPostCommentRepository;
import com.example.ddingsroom.community_post.repository.CommunityPostRepository;
import com.example.ddingsroom.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommunityPostCommentService {

    private final CommunityPostCommentRepository repository;
    private final CommunityPostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommunityPostCommentService(CommunityPostCommentRepository repository,
                                     CommunityPostRepository postRepository,
                                     UserRepository userRepository) {
        this.repository = repository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // 댓글 또는 대댓글 생성
    public BaseResponseDTO createComment(CommunityPostCommentRequestDTO dto) {
        try {
            // 사용자 존재 확인
            if (!userRepository.existsById(dto.getUserId().intValue())) {
                return BaseResponseDTO.error("존재하지 않는 사용자입니다.");
            }

            // 게시글 존재 확인
            if (!postRepository.existsById(dto.getPostId())) {
                return BaseResponseDTO.error("존재하지 않는 게시글입니다.");
            }

            // 대댓글인 경우 부모 댓글 존재 여부 확인
            if (dto.getParentCommentId() != null) {
                Optional<CommunityPostCommentEntity> parentComment = repository.findById(dto.getParentCommentId());
                if (parentComment.isEmpty()) {
                    return BaseResponseDTO.error("부모 댓글을 찾을 수 없습니다.");
                }
                // 대댓글의 대댓글은 허용하지 않음 (2단계 제한)
                if (parentComment.get().getParentCommentId() != null) {
                    return BaseResponseDTO.error("대댓글에는 더 이상 댓글을 달 수 없습니다.");
                }
            }

            CommunityPostCommentEntity entity = new CommunityPostCommentEntity();
            entity.setPostId(dto.getPostId());
            entity.setUserId(dto.getUserId());
            entity.setCommentContent(dto.getCommentContent());
            entity.setParentCommentId(dto.getParentCommentId());

            CommunityPostCommentEntity savedEntity = repository.save(entity);
            CommunityPostCommentResponseDTO responseDTO = convertToResponseDTO(savedEntity);

            String message = dto.getParentCommentId() != null ?
                    "대댓글이 성공적으로 생성되었습니다!" : "댓글이 성공적으로 생성되었습니다!";

            return BaseResponseDTO.success(message, responseDTO);

        } catch (Exception e) {
            return BaseResponseDTO.error("댓글 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 댓글 수정
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

    // 댓글 삭제 (대댓글이 있는 경우 확인)
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

            // 해당 댓글에 대댓글이 있는지 확인
            List<CommunityPostCommentEntity> replies = repository.findByParentCommentId(dto.getCommentId());
            if (!replies.isEmpty()) {
                return BaseResponseDTO.error("대댓글이 있는 댓글은 삭제할 수 없습니다. 먼저 대댓글을 삭제해주세요.");
            }

            repository.deleteById(dto.getCommentId());
            return BaseResponseDTO.success("댓글이 성공적으로 삭제되었습니다!");

        } catch (Exception e) {
            return BaseResponseDTO.error("댓글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 게시글의 모든 댓글 조회 (계층형 구조)
    public BaseResponseDTO getCommentsByPostId(Long postId) {
        try {
            List<CommunityPostCommentEntity> comments = repository.findByPostIdOrderByHierarchy(postId);
            List<CommunityPostCommentResponseDTO> responseDTOs = buildHierarchicalComments(comments);

            return BaseResponseDTO.success("댓글 목록 조회 성공", responseDTOs);
        } catch (Exception e) {
            return BaseResponseDTO.error("댓글 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 댓글의 대댓글들 조회
    public BaseResponseDTO getRepliesByCommentId(Long commentId) {
        try {
            List<CommunityPostCommentEntity> replies = repository.findByParentCommentId(commentId);
            List<CommunityPostCommentResponseDTO> responseDTOs = replies.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            return BaseResponseDTO.success("대댓글 목록 조회 성공", responseDTOs);
        } catch (Exception e) {
            return BaseResponseDTO.error("대댓글 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 단일 댓글 조회
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

    // 특정 사용자의 모든 댓글 조회 (필터링 옵션 포함) - 통합된 메서드
    public BaseResponseDTO getCommentsByUserId(Long userId, String type, Long postId) {
        try {
            List<CommunityPostCommentEntity> comments;

            if (postId != null) {
                // 특정 게시글에서의 사용자 댓글
                if ("comment".equals(type)) {
                    comments = repository.findByUserIdAndPostIdAndParentCommentIdIsNull(userId, postId);
                } else if ("reply".equals(type)) {
                    comments = repository.findByUserIdAndPostIdAndParentCommentIdIsNotNull(userId, postId);
                } else {
                    comments = repository.findByUserIdAndPostId(userId, postId);
                }
            } else {
                // 전체 게시글에서의 사용자 댓글
                if ("comment".equals(type)) {
                    comments = repository.findByUserIdAndParentCommentIdIsNull(userId);
                } else if ("reply".equals(type)) {
                    comments = repository.findByUserIdAndParentCommentIdIsNotNull(userId);
                } else {
                    // 수정: findByUserId 사용
                    comments = repository.findByUserId(userId);
                }
            }

            List<CommunityPostCommentResponseDTO> responseDTOs = comments.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            String message = String.format("사용자 %d의 댓글 목록 조회 성공 (총 %d개)", userId, responseDTOs.size());
            return BaseResponseDTO.success(message, responseDTOs);

        } catch (Exception e) {
            return BaseResponseDTO.error("사용자 댓글 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 사용자의 모든 댓글 조회 (단순 버전)
    public BaseResponseDTO getCommentsByUserId(Long userId) {
        try {
            // 수정: findByUserId 사용 (기존에 있는 메서드)
            List<CommunityPostCommentEntity> comments = repository.findByUserId(userId);
            List<CommunityPostCommentResponseDTO> responseDTOs = comments.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            return BaseResponseDTO.success("사용자 댓글 목록 조회 성공", responseDTOs);
        } catch (Exception e) {
            return BaseResponseDTO.error("사용자 댓글 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 사용자의 일반댓글만 조회
    public BaseResponseDTO getOnlyCommentsByUserId(Long userId) {
        try {
            List<CommunityPostCommentEntity> comments = repository.findByUserIdAndParentCommentIdIsNull(userId);
            List<CommunityPostCommentResponseDTO> responseDTOs = comments.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            return BaseResponseDTO.success("사용자의 일반댓글 조회 성공", responseDTOs);
        } catch (Exception e) {
            return BaseResponseDTO.error("사용자 일반댓글 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 사용자의 대댓글만 조회
    public BaseResponseDTO getOnlyRepliesByUserId(Long userId) {
        try {
            List<CommunityPostCommentEntity> replies = repository.findByUserIdAndParentCommentIdIsNotNull(userId);
            List<CommunityPostCommentResponseDTO> responseDTOs = replies.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            return BaseResponseDTO.success("사용자의 대댓글 조회 성공", responseDTOs);
        } catch (Exception e) {
            return BaseResponseDTO.error("사용자 대댓글 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 특정 사용자가 특정 게시글에 단 댓글들
    public BaseResponseDTO getCommentsByUserIdAndPostId(Long userId, Long postId) {
        try {
            List<CommunityPostCommentEntity> comments = repository.findByUserIdAndPostId(userId, postId);
            List<CommunityPostCommentResponseDTO> responseDTOs = comments.stream()
                    .map(this::convertToResponseDTO)
                    .collect(Collectors.toList());

            String message = String.format("사용자 %d가 게시글 %d에 단 댓글 조회 성공", userId, postId);
            return BaseResponseDTO.success(message, responseDTOs);
        } catch (Exception e) {
            return BaseResponseDTO.error("사용자별 게시글 댓글 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // 계층형 댓글 구조 생성
    private List<CommunityPostCommentResponseDTO> buildHierarchicalComments(List<CommunityPostCommentEntity> comments) {
        List<CommunityPostCommentResponseDTO> result = new ArrayList<>();

        // 최상위 댓글들 먼저 처리
        for (CommunityPostCommentEntity comment : comments) {
            if (comment.getParentCommentId() == null) {
                CommunityPostCommentResponseDTO dto = convertToResponseDTO(comment);

                // 해당 댓글의 대댓글들 찾기
                List<CommunityPostCommentResponseDTO> replies = new ArrayList<>();
                for (CommunityPostCommentEntity reply : comments) {
                    if (reply.getParentCommentId() != null &&
                            reply.getParentCommentId().equals(comment.getId())) {
                        replies.add(convertToResponseDTO(reply));
                    }
                }
                dto.setReplies(replies);
                dto.setReplyCount((long) replies.size());
                result.add(dto);
            }
        }

        return result;
    }

    // Entity를 ResponseDTO로 변환
    private CommunityPostCommentResponseDTO convertToResponseDTO(CommunityPostCommentEntity entity) {
        CommunityPostCommentResponseDTO dto = new CommunityPostCommentResponseDTO();
        dto.setId(entity.getId());
        dto.setPostId(entity.getPostId());
        dto.setUserId(entity.getUserId());
        dto.setCommentContent(entity.getCommentContent());
        dto.setParentCommentId(entity.getParentCommentId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setMessage("성공");

        // 댓글 타입 설정
        dto.setCommentType(entity.getParentCommentId() == null ? "COMMENT" : "REPLY");

        // 대댓글 개수 설정 (일반 댓글인 경우에만)
        if (entity.getParentCommentId() == null) {
            dto.setReplyCount(repository.countByParentCommentId(entity.getId()));
        } else {
            dto.setReplyCount(0L);
        }

        return dto;
    }

    // 사용자의 모든 댓글 삭제 (회원 탈퇴 시 사용)
    @Transactional
    public void deleteAllUserComments(Long userId) {
        try {
            repository.deleteByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("사용자 댓글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
