package com.example.ddingsroom.user.controller;

import com.example.ddingsroom.user.dto.*;
import com.example.ddingsroom.user.service.JoinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/user", produces="application/json; charset=utf8")
public class JoinController {
    private final JoinService joinService;

    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    @GetMapping("/test")
    public ResponseEntity<ResponseDTO> test(){
        return ResponseEntity.ok(new ResponseDTO("테스트 API가 정상적으로 작동합니다."));
    }

    @PostMapping("/join")
    public ResponseEntity<ResponseDTO> joinProcess(@RequestBody JoinDTO joinDTO) {
        return joinService.joinProcess(joinDTO);
    }

    @PostMapping("/modify-password")
    public ResponseEntity<ResponseDTO> modifyPassword(@RequestBody ModifyPasswordDTO modifyPasswordDTO) {
        return joinService.modifyPassword(modifyPasswordDTO);
    }

    @PostMapping("/code-send")
    public ResponseEntity<ResponseDTO> codeSend(@RequestBody CodeSendDTO codeSendDTO) {
        return joinService.autentication1(codeSendDTO);
    }

    @PostMapping("/code-verify")
    public ResponseEntity<ResponseDTO> codeVerify(@RequestBody CodeVerifyDTO codeVerifyDTO) {
        return joinService.verifyCode(codeVerifyDTO);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ResponseDTO> signUp(@RequestBody SignUpDTO signUpDTO) {
        return joinService.signUp(signUpDTO);
    }

    @GetMapping("/mypage/{userId}")
    public ResponseEntity<?> getMyPage(@PathVariable Integer userId) {
        return joinService.getMyPage(userId);
    }
}
