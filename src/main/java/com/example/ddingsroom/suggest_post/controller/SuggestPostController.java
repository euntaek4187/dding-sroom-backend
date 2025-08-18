package com.example.ddingsroom.suggest_post.controller;

import com.example.ddingsroom.suggest_post.dto.SuggestPostCreateRequestDTO;
import com.example.ddingsroom.suggest_post.dto.SuggestPostDeleteRequestDTO;
import com.example.ddingsroom.suggest_post.dto.SuggestPostResponseDTO;
import com.example.ddingsroom.suggest_post.dto.SuggestPostUpdateRequestDTO;
import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post.service.SuggestPostSevice;
import com.example.ddingsroom.config.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/suggestions")
@RequiredArgsConstructor
public class SuggestPostController {

    private final SuggestPostSevice suggestPostSevice;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<Map<String, String>> createSuggestPost(@Valid @RequestBody SuggestPostCreateRequestDTO request){
        Map<String, String> response = new HashMap<>();
        try{
            Long authenticatedUserId = securityUtils.getAuthenticatedUserId();
            suggestPostSevice.createSuggestPost(request, authenticatedUserId);
            response.put("message", "건의가 성공적으로 생성되었습니다!");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (SecurityException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            response.put("error", "건의 생성 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> updateSuggestPost(@Valid @RequestBody SuggestPostUpdateRequestDTO request) {

        Map<String, String> response = new HashMap<>();

        try{
            Long authenticatedUserId = securityUtils.getAuthenticatedUserId();
            suggestPostSevice.updateSuggestPost(request, authenticatedUserId);
            response.put("message", "건의가 성공적으로 업데이트되었습니다!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            response.put("error", "건의 업데이트 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{suggestId}")
    public ResponseEntity<Map<String, String>> deleteSuggestPost(
            @PathVariable Long suggestId) {
        Map<String, String> response = new HashMap<>();
        try {
            Long authenticatedUserId = securityUtils.getAuthenticatedUserId();
            suggestPostSevice.deleteSuggestPost(suggestId, authenticatedUserId);
            response.put("message", "건의가 성공적으로 삭제되었습니다!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SecurityException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            response.put("error", "건의 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> retrieveSuggestPosts(
            @RequestParam(name = "suggest_id", required = false) Optional<Long> suggestId,
            @RequestParam(name = "user_id", required = false) Optional<Long> userId,
            @RequestParam(name = "category", required = false) Optional<String> category,
            @RequestParam(name = "location", required = false) Optional<String> location,
            @RequestParam(name = "is_answered", required = false) Optional<Boolean> isAnswered) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<SuggestPostResponseDTO> suggestions = suggestPostSevice.retrieveSuggestPosts(suggestId, userId, category, location, isAnswered);

            if (suggestions.isEmpty() && (suggestId.isPresent() || userId.isPresent() || category.isPresent() || location.isPresent() || isAnswered.isPresent())) {
                response.put("message", "검색 조건에 해당하는 건의를 찾을 수 없습니다.");
            } else if (suggestions.isEmpty()) {
                response.put("message", "등록된 건의가 없습니다.");
            } else {
                response.put("suggestions", suggestions);
                response.put("message", "성공");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("error", "건의 조회 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}