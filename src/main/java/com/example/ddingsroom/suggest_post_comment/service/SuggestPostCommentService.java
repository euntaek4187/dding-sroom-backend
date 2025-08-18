package com.example.ddingsroom.suggest_post_comment.service;

import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post.repository.SuggestPostRepository;
import com.example.ddingsroom.suggest_post_comment.dto.SuggestPostCommentCreateRequestDTO;
import com.example.ddingsroom.suggest_post_comment.dto.SuggestPostCommentResponseDTO;
import com.example.ddingsroom.suggest_post_comment.dto.SuggestPostCommentUpdateRequestDTO;
import com.example.ddingsroom.suggest_post_comment.dto.SuggestPostCommentDeleteRequestDTO;
import com.example.ddingsroom.suggest_post_comment.entity.SuggestPostCommentEntity;
import com.example.ddingsroom.suggest_post_comment.repository.SuggestPostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuggestPostCommentService {

    private final SuggestPostCommentRepository suggestPostCommentRepository;
    private final SuggestPostRepository suggestPostRepository;

    @Transactional
    public void createComment(SuggestPostCommentCreateRequestDTO request, Long authenticatedUserId) {
        SuggestPostEntity suggestPost = suggestPostRepository.findById(request.getSuggestPostId())
                .orElseThrow(() -> new IllegalArgumentException("건의 게시물을 찾을 수 없습니다. ID: " + request.getSuggestPostId()));

        if (suggestPost.getComment() != null) {
            throw new IllegalArgumentException("해당 건의 게시물(ID: " + request.getSuggestPostId() + ")에는 이미 댓글이 존재합니다.");
        }

        SuggestPostCommentEntity newComment = new SuggestPostCommentEntity(
                suggestPost,
                authenticatedUserId,
                request.getAnswerContent()
        );

        suggestPost.setComment(newComment);
        suggestPostRepository.save(suggestPost);

        if (!suggestPost.isAnswered()) {
            suggestPost.setAnswered(true);
        }
    }

    @Transactional
    public void updateComment(SuggestPostCommentUpdateRequestDTO request, Long authenticatedUserId) {
        SuggestPostCommentEntity existingComment = suggestPostCommentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + request.getCommentId()));

        if (!existingComment.getUserId().equals(authenticatedUserId)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다. (댓글 작성자 ID: " + existingComment.getUserId() + ", 요청된 사용자 ID: " + authenticatedUserId + ")");
        }

        existingComment.setAnswerContent(request.getAnswerContent());
        suggestPostCommentRepository.save(existingComment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long authenticatedUserId) {
        SuggestPostCommentEntity existingComment = suggestPostCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));

        if (!existingComment.getUserId().equals(authenticatedUserId)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다. (댓글 작성자 ID: " + existingComment.getUserId() + ", 요청된 사용자 ID: " + authenticatedUserId + ")");
        }

        SuggestPostEntity suggestPost = existingComment.getSuggestPost();

        if (suggestPost != null) {
            suggestPost.removeComment();
            suggestPost.setAnswered(false);

            suggestPostRepository.save(suggestPost);
        }

        suggestPostCommentRepository.delete(existingComment);
    }

    @Transactional(readOnly = true)
    public List<SuggestPostCommentResponseDTO> getSuggestCommentsByPostId(Long postId) {
        suggestPostRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("건의 게시물을 찾을 수 없습니다. ID: " + postId));

        SuggestPostCommentEntity comment = suggestPostRepository.findById(postId)
                .map(SuggestPostEntity::getComment)
                .orElse(null);

        if (comment != null) {
            return Collections.singletonList(new SuggestPostCommentResponseDTO(comment));
        } else {
            return Collections.emptyList();
        }
    }
}