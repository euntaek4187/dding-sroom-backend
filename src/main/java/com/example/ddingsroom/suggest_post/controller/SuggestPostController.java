package com.example.ddingsroom.suggest_post.controller;

import com.example.ddingsroom.suggest_post.dto.SuggestPostDTO;
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
}
