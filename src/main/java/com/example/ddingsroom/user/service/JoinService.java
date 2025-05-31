package com.example.ddingsroom.user.service;

import com.example.ddingsroom.user.entity.UserEntity;
import com.example.ddingsroom.user.entity.VerificationCode;
import com.example.ddingsroom.user.dto.*;
import com.example.ddingsroom.user.repository.UserRepository;
import com.example.ddingsroom.user.repository.VerificationCodeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class JoinService {
    private final VerificationCodeRepository verificationCodeRepository;
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    // 이메일 정규식 패턴
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public String generateRandomCode() {
        return String.format("%06d", secureRandom.nextInt(1000000));
    }

    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder,
                       JavaMailSender javaMailSender, VerificationCodeRepository verificationCodeRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.javaMailSender = javaMailSender;
        this.verificationCodeRepository = verificationCodeRepository;
    }

    public ResponseEntity<ResponseDTO> joinProcess(JoinDTO joinDTO) {
        try {
            // 입력값 유효성 검사
            if (!StringUtils.hasText(joinDTO.getUsername())) {
                return ResponseEntity.badRequest().body(new ResponseDTO("사용자명을 입력해주세요."));
            }
            if (!StringUtils.hasText(joinDTO.getPassword())) {
                return ResponseEntity.badRequest().body(new ResponseDTO("비밀번호를 입력해주세요."));
            }
            if (joinDTO.getPassword().length() < 6) {
                return ResponseEntity.badRequest().body(new ResponseDTO("비밀번호는 6자 이상이어야 합니다."));
            }

            String username = joinDTO.getUsername();
            String password = joinDTO.getPassword();

            Boolean isExist = userRepository.existsByUsername(username);
            if (isExist) {
                return ResponseEntity.badRequest().body(new ResponseDTO("이미 존재하는 사용자명입니다."));
            }

            UserEntity data = new UserEntity();
            data.setUsername(username);
            data.setPassword(bCryptPasswordEncoder.encode(password));
            data.setRole("ROLE_ADMIN");
            userRepository.save(data);

            return ResponseEntity.ok(new ResponseDTO("관리자 계정이 성공적으로 생성되었습니다."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("계정 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    public ResponseEntity<ResponseDTO> autentication1(CodeSendDTO codeSendDTO) {
        try {
            // 입력값 유효성 검사
            if (!StringUtils.hasText(codeSendDTO.getEmail())) {
                return ResponseEntity.badRequest().body(new ResponseDTO("이메일을 입력해주세요."));
            }
            if (!isValidEmail(codeSendDTO.getEmail())) {
                return ResponseEntity.badRequest().body(new ResponseDTO("올바른 이메일 형식이 아닙니다."));
            }

            // 기존 인증코드 삭제
            Optional<VerificationCode> existingCode = verificationCodeRepository.findByEmail(codeSendDTO.getEmail());
            existingCode.ifPresent(verificationCodeRepository::delete);

            String randomCode = generateRandomCode();
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(codeSendDTO.getEmail());
            mailMessage.setSubject("띵스룸 이메일 인증 코드");
            mailMessage.setText("인증 코드: " + randomCode + "\n\n이 코드는 5분간 유효합니다.");
            javaMailSender.send(mailMessage);

            VerificationCode verificationCode = new VerificationCode(randomCode, codeSendDTO.getEmail());
            verificationCodeRepository.save(verificationCode);

            return ResponseEntity.ok(new ResponseDTO("인증 코드가 " + codeSendDTO.getEmail() + "로 전송되었습니다."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("이메일 전송 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO> verifyCode(CodeVerifyDTO codeVerifyDTO) {
        try {
            // 입력값 유효성 검사
            if (!StringUtils.hasText(codeVerifyDTO.getEmail())) {
                return ResponseEntity.badRequest().body(new ResponseDTO("이메일을 입력해주세요."));
            }
            if (!StringUtils.hasText(codeVerifyDTO.getCode())) {
                return ResponseEntity.badRequest().body(new ResponseDTO("인증 코드를 입력해주세요."));
            }

            String email = codeVerifyDTO.getEmail();
            String code = codeVerifyDTO.getCode();

            Optional<VerificationCode> verificationCodeOptional = verificationCodeRepository.findByEmail(email);

            if (verificationCodeOptional.isPresent()) {
                VerificationCode verificationCode = verificationCodeOptional.get();

                if (verificationCode.getCode().equals(code)) {
                    verificationCodeRepository.delete(verificationCode);
                    return ResponseEntity.ok(new ResponseDTO("이메일 인증이 성공적으로 완료되었습니다."));
                } else {
                    return ResponseEntity.badRequest().body(new ResponseDTO("인증 코드가 일치하지 않습니다."));
                }
            } else {
                return ResponseEntity.badRequest().body(new ResponseDTO("인증 코드가 존재하지 않거나 만료되었습니다."));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("인증 확인 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    public ResponseEntity<ResponseDTO> signUp(SignUpDTO signUpDTO) {
        try {
            // 입력값 유효성 검사
            if (!StringUtils.hasText(signUpDTO.getEmail())) return ResponseEntity.badRequest().body(new ResponseDTO("이메일을 입력해주세요."));
            if (!isValidEmail(signUpDTO.getEmail())) return ResponseEntity.badRequest().body(new ResponseDTO("올바른 이메일 형식이 아닙니다."));
            if (!StringUtils.hasText(signUpDTO.getUsername())) return ResponseEntity.badRequest().body(new ResponseDTO("사용자명을 입력해주세요."));
            if (!StringUtils.hasText(signUpDTO.getPassword())) return ResponseEntity.badRequest().body(new ResponseDTO("비밀번호를 입력해주세요."));
            if (signUpDTO.getPassword().length() < 6) return ResponseEntity.badRequest().body(new ResponseDTO("비밀번호는 6자 이상이어야 합니다."));

            // 중복 체크
            if (userRepository.existsByEmail(signUpDTO.getEmail())) return ResponseEntity.badRequest().body(new ResponseDTO("이미 가입된 이메일입니다."));
            if (userRepository.existsByUsername(signUpDTO.getUsername())) return ResponseEntity.badRequest().body(new ResponseDTO("이미 사용 중인 사용자명입니다."));

            UserEntity newUser = new UserEntity();
            newUser.setEmail(signUpDTO.getEmail());
            newUser.setUsername(signUpDTO.getUsername());
            newUser.setPassword(bCryptPasswordEncoder.encode(signUpDTO.getPassword()));
            newUser.setAge(signUpDTO.getAge());
            newUser.setStudentNumber(signUpDTO.getStudentNumber());
            newUser.setRole("ROLE_USER");
            newUser.setState("normal");
            newUser.setRegistrationDate(LocalDateTime.now());
            userRepository.save(newUser);

            return ResponseEntity.ok(new ResponseDTO("회원가입이 성공적으로 완료되었습니다."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("회원가입 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    public ResponseEntity<ResponseDTO> modifyPassword(ModifyPasswordDTO modifyPasswordDTO) {
        try {
            // 입력값 유효성 검사
            if (!StringUtils.hasText(modifyPasswordDTO.getEmail())) return ResponseEntity.badRequest().body(new ResponseDTO("이메일을 입력해주세요."));
            if (!isValidEmail(modifyPasswordDTO.getEmail())) return ResponseEntity.badRequest().body(new ResponseDTO("올바른 이메일 형식이 아닙니다."));
            if (!StringUtils.hasText(modifyPasswordDTO.getPassword())) return ResponseEntity.badRequest().body(new ResponseDTO("새 비밀번호를 입력해주세요."));
            if (modifyPasswordDTO.getPassword().length() < 6) return ResponseEntity.badRequest().body(new ResponseDTO("비밀번호는 6자 이상이어야 합니다."));

            boolean emailExists = userRepository.existsByEmail(modifyPasswordDTO.getEmail());
            if (!emailExists) return ResponseEntity.badRequest().body(new ResponseDTO("존재하지 않는 이메일입니다."));

            Optional<UserEntity> userEntityOptional = Optional.ofNullable(userRepository.findByEmail(modifyPasswordDTO.getEmail()));
            if (userEntityOptional.isPresent()) {
                UserEntity user = userEntityOptional.get();
                user.setPassword(bCryptPasswordEncoder.encode(modifyPasswordDTO.getPassword()));
                userRepository.save(user);
                return ResponseEntity.ok(new ResponseDTO("비밀번호가 성공적으로 변경되었습니다."));
            } else {
                return ResponseEntity.badRequest().body(new ResponseDTO("사용자 정보를 찾을 수 없습니다."));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("비밀번호 변경 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    private boolean isValidEmail(String email) {
        return pattern.matcher(email).matches();
    }

    public ResponseEntity<?> getMyPage(Integer userId) {
        try {
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ResponseDTO("사용자 ID를 입력해주세요."));
            }

            if (userId <= 0) {
                return ResponseEntity.badRequest().body(new ResponseDTO("올바른 사용자 ID를 입력해주세요."));
            }

            Optional<UserEntity> userEntityOptional = userRepository.findById(userId);

            if (userEntityOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseDTO("해당 사용자를 찾을 수 없습니다."));
            }

            UserEntity userEntity = userEntityOptional.get();

            MyPageDTO myPageDTO = new MyPageDTO(userEntity);

            if (myPageDTO.getEmail() == null) {
                myPageDTO.setEmail("");
            }
            if (myPageDTO.getUsername() == null) {
                myPageDTO.setUsername("");
            }
            if (myPageDTO.getAge() == null) {
                myPageDTO.setAge("");
            }
            if (myPageDTO.getStudentNumber() == null) {
                myPageDTO.setStudentNumber("");
            }
            if (myPageDTO.getRole() == null) {
                myPageDTO.setRole("ROLE_USER");
            }
            if (myPageDTO.getState() == null) {
                myPageDTO.setState("normal");
            }
            if (myPageDTO.getRegistrationDate() == null) {
                myPageDTO.setRegistrationDate(LocalDateTime.now());
            }
            return ResponseEntity.ok(myPageDTO);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDTO("올바른 사용자 ID 형식이 아닙니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDTO("마이페이지 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}