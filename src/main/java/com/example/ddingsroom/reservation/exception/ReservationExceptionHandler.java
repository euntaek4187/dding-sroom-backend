package com.example.ddingsroom.reservation.exception;

import com.example.ddingsroom.reservation.dto.BaseResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.format.DateTimeParseException;

@ControllerAdvice(basePackages = "com.example.ddingsroom.reservation.controller")
public class ReservationExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ReservationExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<BaseResponseDTO> handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.error("EntityNotFoundException 발생: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new BaseResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<BaseResponseDTO> handleDateTimeParseException(DateTimeParseException ex) {
        logger.error("DateTimeParseException 발생: {}", ex.getMessage());
        logger.error("시간 파싱 중 오류가 발생했습니다. 입력 값을 확인하세요.", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponseDTO("날짜 또는 시간 형식이 올바르지 않습니다. 시간 형식을 확인해주세요."));
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponseDTO> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.error("HttpMessageNotReadableException 발생: {}", ex.getMessage());
        if (ex.getMessage().contains("LocalTime")) {
            logger.error("시간 형식이 잘못되었습니다. 24:00은 00:00으로 입력해주세요.", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponseDTO("시간 형식이 올바르지 않습니다. 24:00은 00:00으로 입력하세요."));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponseDTO("요청 데이터 형식이 올바르지 않습니다. 입력값을 확인해주세요."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("IllegalArgumentException 발생: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponseDTO(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDTO> handleGeneralException(Exception ex) {
        logger.error("예약 처리 중 예상치 못한 오류 발생: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponseDTO("서버 오류가 발생했습니다. 나중에 다시 시도해주세요."));
    }
}