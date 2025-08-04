package com.example.ddingsroom.user.jwt;
import com.example.ddingsroom.user.dto.CustomUserDetails;
import com.example.ddingsroom.user.entity.RefreshEntity;
import com.example.ddingsroom.user.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.io.IOException; // IOException 임포트
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import com.fasterxml.jackson.databind.ObjectMapper; // ObjectMapper 임포트
import com.example.ddingsroom.user.dto.LoginDTO; // LoginDTO 임포트 (LoginFilter는 JoinDTO가 아닌 로그인용 DTO를 사용하는 것이 좋음)


public class LoginFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper 인스턴스 생성

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        // setUsernameParameter("email"); // 이미 설정되어 있음
        // setPasswordParameter("password"); // 필요 시 추가. 기본은 "password"
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("LoginFilter: attemptAuthentication 메소드 시작."); // 시작 로그

        String email = null;
        String password = null;

        try {
            // 요청 본문을 LoginDTO로 읽기 시도 (JoinDTO가 아닌 로그인용 DTO를 사용하는 것이 적절)
            // LoginDTO는 email과 password 필드를 가집니다.
            LoginDTO loginDTO = objectMapper.readValue(request.getInputStream(), LoginDTO.class);
            email = loginDTO.getEmail();
            password = loginDTO.getPassword();

            System.out.println("LoginFilter: 추출된 Email: " + email); // 추출된 email 로그
            // System.out.println("LoginFilter: 추출된 Password: " + password); // 비밀번호는 보안상 로깅에 주의! 디버깅 시에만.

        } catch (IOException e) {
            System.err.println("LoginFilter: 요청 본문 파싱 오류: " + e.getMessage()); // 오류 로그
            throw new AuthenticationServiceException("Failed to parse authentication request body", e); // 예외 타입 변경
        }

        System.out.println("LoginFilter: UsernamePasswordAuthenticationToken 생성 시도."); // 토큰 생성 전 로그
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password, null);
        System.out.println("LoginFilter: AuthenticationManager.authenticate 호출."); // authenticate 호출 전 로그
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        System.out.println("LoginFilter: successfulAuthentication 메소드 시작."); // 성공 로그
        // 사용자 정보 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        int id = userDetails.getUserEntity().getId();
        String email = userDetails.getUserEntity().getEmail();

        // 역할 정보 추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // ID와 이메일이 포함된 토큰 생성
        String access = jwtUtil.createJwt("access", username, role, id, email, 600000L); // 10분
        String refresh = jwtUtil.createJwt("refresh", username, role, id, email, 86400000L); // 24시간

        // Refresh 토큰 저장
        addRefreshEntity(email, refresh, 86400000L);

        // 응답 설정
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());
        System.out.println("LoginFilter: 인증 성공 - 사용자: " + username + ", 역할: " + role); // 사용자 정보 로그
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        System.out.println("LoginFilter: unsuccessfulAuthentication 메소드 시작. 실패 원인: " + failed.getMessage()); // 실패 로그
        response.setStatus(401);
        try { // 실패 응답에 메시지 추가 (선택 사항)
            response.getWriter().write("Authentication Failed: " + failed.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void addRefreshEntity(String email, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(email);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }
}
