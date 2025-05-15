package com.example.ddingsroom.user.controller;

import com.example.ddingsroom.reservation.dto.*;
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
    public String test(){
        return "test";
    }
    @PostMapping("/join")
    public String joinProcess(JoinDTO joinDTO) {
        System.out.println(joinDTO.getUsername());
        joinService.joinProcess(joinDTO);
        return "ok";
    }
    @PostMapping("/modify-password")
    public ResponseEntity<String> modifyPassword(@RequestBody ModifyPasswordDTO modifyPasswordDTO) {
        return joinService.modifyPassword(modifyPasswordDTO);
    }
    @PostMapping("/code-send")
    public ResponseEntity<String> codeSend(@RequestBody CodeSendDTO codeSendDTO) {
        return joinService.autentication1(codeSendDTO);
    }
    @PostMapping("/code-verify")
    public ResponseEntity<String> codeVerify(@RequestBody CodeVerifyDTO codeVerifyDTO) {
        return joinService.verifyCode(codeVerifyDTO);
    }
    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@RequestBody SignUpDTO signUpDTO) {
        return joinService.signUp(signUpDTO);
    }
}