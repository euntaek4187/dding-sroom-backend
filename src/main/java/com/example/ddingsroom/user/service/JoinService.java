package com.example.ddingsroom.user.service;
import com.example.ddingsroom.reservation.dto.*;
import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.entity.VerificationCode;
import com.example.ddingsroom.user.dto.*;
import com.example.ddingsroom.user.repository.UserRepository;
import com.example.ddingsroom.user.repository.VerificationCodeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class JoinService {
    private final VerificationCodeRepository verificationCodeRepository;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    public String generateRandomCode() {
        return String.format("%06d", secureRandom.nextInt(1000000));
    }
    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JavaMailSender javaMailSender, VerificationCodeRepository verificationCodeRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.javaMailSender = javaMailSender;
        this.verificationCodeRepository = verificationCodeRepository;
    }

    public void joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        Boolean isExist = userRepository.existsByUsername(username);

        if (isExist) {
            return;
        }

        UserEntity data = new UserEntity();
        data.setUsername(username);
        // 패스워드 그냥 넣으면 안되고 미리만든 인코딩 메서드 통해서 인코딩 하고 저장해야함.
        data.setPassword(bCryptPasswordEncoder.encode(password));
        data.setRole("ROLE_ADMIN");
        userRepository.save(data);
    }

    public ResponseEntity<String> autentication1(CodeSendDTO codeSendDTO) {
        Optional<VerificationCode> existingCode = verificationCodeRepository.findByEmail(codeSendDTO.getEmail());
        existingCode.ifPresent(verificationCodeRepository::delete);

        String randomCode = generateRandomCode();
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(codeSendDTO.getEmail());
        mailMessage.setSubject("subject");
        mailMessage.setText(String.valueOf(randomCode));
        javaMailSender.send(mailMessage);

        VerificationCode verificationCode = new VerificationCode(randomCode, codeSendDTO.getEmail());
        verificationCodeRepository.save(verificationCode);

        return ResponseEntity.ok("Received email: " + codeSendDTO.getEmail());
    }

    @Transactional
    public ResponseEntity<String> verifyCode(CodeVerifyDTO codeVerifyDTO) {
        String email = codeVerifyDTO.getEmail();
        String code = codeVerifyDTO.getCode();

        Optional<VerificationCode> verificationCodeOptional = verificationCodeRepository.findByEmail(email);

        if (verificationCodeOptional.isPresent()) {
            VerificationCode verificationCode = verificationCodeOptional.get();

            if (verificationCode.getCode().equals(code)) {
                verificationCodeRepository.delete(verificationCode);
                return ResponseEntity.ok("인증이 완료되었습니다.");
            } else {
                return ResponseEntity.badRequest().body("인증에 실패했습니다.");
            }
        } else {
            return ResponseEntity.badRequest().body("인증에 실패했습니다.");
        }
    }

    public ResponseEntity<String> signUp(SignUpDTO signUpDTO) {
        boolean emailExists = userRepository.existsByUsername(signUpDTO.getEmail());
        if (emailExists) return ResponseEntity.badRequest().body("이미 가입된 계정입니다.");
        UserEntity newUser = new UserEntity();
        newUser.setEmail(signUpDTO.getEmail());
        newUser.setUsername(signUpDTO.getUsername());
        newUser.setPassword(bCryptPasswordEncoder.encode(signUpDTO.getPassword()));
        newUser.setAge(signUpDTO.getAge());
        newUser.setStudentNumber(signUpDTO.getStudentNumber());
        newUser.setRole("ROLE_USER");
        newUser.setState("nomal");
        newUser.setRegistrationDate(LocalDateTime.now());
        userRepository.save(newUser);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
    public ResponseEntity<String> modifyPassword(ModifyPasswordDTO modifyPasswordDTO) {
        boolean emailExists = userRepository.existsByEmail(modifyPasswordDTO.getEmail());
        if (!emailExists) return ResponseEntity.badRequest().body("존재하지 않는 email 입니다.");
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(modifyPasswordDTO.getEmail());
        if (userEntityOptional.isPresent()) {
            UserEntity user = userEntityOptional.get();
            user.setPassword(bCryptPasswordEncoder.encode(modifyPasswordDTO.getPassword()));
            userRepository.save(user);
        }
        return ResponseEntity.ok("비밀번호 재설정이 완료되었습니다.");
    }
}