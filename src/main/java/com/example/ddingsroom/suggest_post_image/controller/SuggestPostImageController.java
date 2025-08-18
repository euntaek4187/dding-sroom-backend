package com.example.ddingsroom.suggest_post_image.controller;

import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.repository.UserRepository;
import com.example.ddingsroom.suggest_post_image.dto.SuggestPostImageUploadRequestDTO;
import com.example.ddingsroom.suggest_post_image.dto.SuggestPostImageDeleteRequestDTO;
import com.example.ddingsroom.suggest_post_image.dto.SuggestPostImageResponseDTO;
import com.example.ddingsroom.suggest_post_image.service.SuggestPostImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/suggestions/images")
@RequiredArgsConstructor
public class SuggestPostImageController {

    private final SuggestPostImageService suggestPostImageService;
    private final UserRepository userRepository;

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new SecurityException("로그인되지 않은 사용자입니다.");
        }
        Object principal = authentication.getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            throw new IllegalStateException("인증된 사용자 principal의 타입을 알 수 없습니다: " + principal.getClass().getName());
        }
        UserEntity userEntity = userRepository.findByUsername(username);
        if (userEntity == null) {
            throw new IllegalStateException("인증된 사용자(" + username + ")의 정보를 데이터베이스에서 찾을 수 없습니다.");
        }
        return (long) userEntity.getId();
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return true;
            }
        }
        return false;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadSuggestImage(
            @RequestPart("request") @Valid SuggestPostImageUploadRequestDTO request,
            @RequestPart("image_file") MultipartFile imageFile) {

        Map<String, Object> response = new HashMap<>();
        try {
            Long authenticatedUserId = getAuthenticatedUserId();
            if (!request.getUserId().equals(authenticatedUserId)) {
                response.put("error", "요청한 사용자 ID와 인증된 사용자 ID가 일치하지 않습니다.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            if (!isAdmin(SecurityContextHolder.getContext().getAuthentication())) {
                response.put("error", "관리자만 이미지를 업로드할 수 있습니다.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            SuggestPostImageResponseDTO uploadedImage = suggestPostImageService.uploadImage(request.getSuggestPostId(), request.getUserId(), imageFile);

            response.put("message", "건의 이미지가 성공적으로 업로드되었습니다!");
            response.put("image_id", uploadedImage.getId());
            response.put("image_url", uploadedImage.getFileUrl());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            response.put("error", "이미지 업로드 중 파일 처리 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            response.put("error", "이미지 업로드 중 예상치 못한 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> deleteSuggestImage(@Valid @RequestBody SuggestPostImageDeleteRequestDTO request) {
        Map<String, String> response = new HashMap<>();
        try {
            Long authenticatedUserId = getAuthenticatedUserId();
            if (!request.getUserId().equals(authenticatedUserId)) {
                response.put("error", "요청한 사용자 ID와 인증된 사용자 ID가 일치하지 않습니다.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }
            if (!isAdmin(SecurityContextHolder.getContext().getAuthentication())) {
                response.put("error", "관리자만 이미지를 삭제할 수 있습니다.");
                return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
            }

            suggestPostImageService.deleteImage(request.getImageId(), request.getUserId());

            response.put("message", "건의 이미지가 성공적으로 삭제되었습니다!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("error", "이미지 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> retrieveSuggestImages(@RequestParam("suggest_post_id") Long suggestPostId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<SuggestPostImageResponseDTO> images = suggestPostImageService.getImagesBySuggestPostId(suggestPostId);

            if (images.isEmpty()) {
                response.put("images", Collections.emptyList());
                response.put("message", "해당 건의에 첨부된 이미지가 없습니다.");
            } else {
                response.put("images", images);
                response.put("message", "성공");
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.put("error", "이미지 조회 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}