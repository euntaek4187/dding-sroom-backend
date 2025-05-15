package com.example.ddingsroom.reservation.util;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
public class ReservationTimeValidator {

    // 예약 시간이 1시간 또는 2시간 단위인지 검증
    public static boolean isValidReservationDuration(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return false;
        }
        
        // endTime이 startTime보다 이전이면 무효
        if (endTime.isBefore(startTime)) {
            return false;
        }
        
        // 예약 시간이 1시간 또는 2시간 단위인지 확인
        long hours = ChronoUnit.HOURS.between(startTime, endTime);
        return hours == 1 || hours == 2;
    }
}