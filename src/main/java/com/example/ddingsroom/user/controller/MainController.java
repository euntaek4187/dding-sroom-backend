package com.example.ddingsroom.user.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Iterator;

@ResponseBody
@Controller
public class MainController {
    @GetMapping("/")
    public String mainP() {

        // 이름 가져오기
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        // 역할 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        return "main Controller: " + name + " role: "+ role;
    }
}
