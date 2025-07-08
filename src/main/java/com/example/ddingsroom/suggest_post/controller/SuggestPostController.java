package com.example.ddingsroom.suggest_post.controller;

import com.example.ddingsroom.suggest_post.dto.SuggestPostDTO;
import com.example.ddingsroom.suggest_post.dto.SuggestPostUpdateRequestDTO;
import com.example.ddingsroom.suggest_post.entity.SuggestPostEntity;
import com.example.ddingsroom.suggest_post.service.SuggestPostSevice;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/suggest_post")
public class SuggestPostController {

    private final SuggestPostSevice suggestPostSevice;

    public SuggestPostController(SuggestPostSevice suggestPostSevice) {
        this.suggestPostSevice = suggestPostSevice;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createSuggestPost(@Valid @RequestBody SuggestPostDTO request){
        Map<String, String> response = new HashMap<>();
        try{
            SuggestPostEntity createdPost = suggestPostSevice.createSuggestPost(request);
            response.put("message", "건의가 성공적으로 생성되었습니다!");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            response.put("error", "건의 생성 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{suggestId}")
    public ResponseEntity<Map<String, String>> updateSuggestPost(
            @PathVariable Long suggestId,
            @Valid @RequestBody SuggestPostUpdateRequestDTO request) {

        Map<String, String> response = new HashMap<>();

        if(!suggestId.equals(request.getSuggestId())) {
            response.put("error", "요청 경로의 ID와 본문의 ID가 일치하지 않습니다.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try{
            SuggestPostEntity updatedPost = suggestPostSevice.updateSuggestPost(suggestId, request);
            response.put("message", "건의가 성공적으로 업데이트되었습니다!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e){
            response.put("error", "건의 업데이트 중 오류가 발생했습니다: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
