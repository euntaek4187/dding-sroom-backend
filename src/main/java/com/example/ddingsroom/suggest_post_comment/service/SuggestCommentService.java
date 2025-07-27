package com.example.ddingsroom.suggest_post_comment.service;

import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post.repository.SuggestPostRepository;
import com.example.ddingsroom.suggest_post_comment.dto.SuggestCommentDTO;
import com.example.ddingsroom.suggest_post_comment.entity.SuggestCommentEntity;
import com.example.ddingsroom.suggest_post_comment.repository.SuggestCommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuggestCommentService {

    private final SuggestCommentRepository suggestCommentRepository;
    private final SuggestPostRepository suggestPostRepository;

    @Transactional
    public void createComment(SuggestCommentDTO request) {
        SuggestPostEntity suggestPost = suggestPostRepository.findById(request.getSuggestPostId())
                .orElseThrow(() -> new IllegalArgumentException("건의 게시물을 찾을 수 없습니다. ID: " + request.getSuggestPostId()));
        if (suggestPost.getComment() != null){
            throw new IllegalArgumentException("해당 건의 게시물(ID: " + request.getSuggestPostId() + ")에는 이미 댓글이 존재합니다.");
        }

        SuggestCommentEntity newComment = new SuggestCommentEntity(
                suggestPost,
                request.getUserId(),
                request.getAnswerContent()
        );

        suggestPost.setComment(newComment);
        suggestPostRepository.save(suggestPost);

        if(!suggestPost.isAnswered()) {
            suggestPost.setAnswered(true);
        }
    }
}
