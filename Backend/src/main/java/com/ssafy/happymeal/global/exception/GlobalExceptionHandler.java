package com.ssafy.happymeal.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException; // URL 파라미터 타입 불일치

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 간단한 에러 응답 DTO
    @Getter
    @AllArgsConstructor
    public static class ErrorResponse {
        private int status;
        private String errorCode; // 선택적: 내부 에러 코드
        private String message;
        private Map<String, String> validationErrors; // 필드별 유효성 검사 오류

        // 일반 오류용 생성자
        public ErrorResponse(HttpStatus status, String errorCode, String message) {
            this.status = status.value();
            this.errorCode = errorCode;
            this.message = message;
        }
        // 유효성 검사 오류용 생성자
        public ErrorResponse(HttpStatus status, String errorCode, String message, Map<String, String> validationErrors) {
            this.status = status.value();
            this.errorCode = errorCode;
            this.message = message;
            this.validationErrors = validationErrors;
        }
    }

    // 커스텀 예외 (예시)
    public static class DuplicateNicknameException extends RuntimeException {
        public DuplicateNicknameException(String message) { super(message); }
    }
    // public static class UserNotFoundException extends RuntimeException { ... }


    /**
     * 1. 닉네임 규칙 위반 (DTO 유효성 검증 실패)
     * 컨트롤러에서 @Valid 어노테이션을 사용한 DTO가 유효성 검증에 실패하면 MethodArgumentNotValidException 발생
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // 어떤 필드에서 어떤 규칙을 위반했는지 상세 로깅
        log.warn("DTO 유효성 검사 실패: {}", errors);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "입력 값 유효성 검사에 실패했습니다.",
                errors
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request
    }

    /**
     * 2. URL 입력 형식 오류 (예: 경로 변수 또는 요청 파라미터 타입 불일치)
     * /api/boards/{boardId} 에서 boardId가 Long 타입이어야 하는데 문자열이 들어온 경우 등
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String parameterName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "알 수 없음";
        Object actualValue = ex.getValue();

        String message = String.format("요청 파라미터 '%s'의 타입이 올바르지 않습니다. '%s' 타입이 필요하지만, 잘못된 값 '%s'이(가) 입력되었습니다.",
                parameterName, requiredType, actualValue);

        log.warn("URL 파라미터 타입 불일치: {}", message);

        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_URL_PARAMETER_TYPE",
                message
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400 Bad Request
    }

    /**
     * 3. 중복 닉네임이 있음 (서비스 계층에서 발생시키는 커스텀 예외)
     */
    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateNicknameException(DuplicateNicknameException ex) {
        log.warn("닉네임 중복 시도: {}", ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.CONFLICT, // 중복은 보통 409 Conflict
                "NICKNAME_DUPLICATE",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // 기타 서비스에서 발생 가능한 UserNotFoundException 등 다른 커스텀 예외 핸들러 추가 가능...

    /**
     * 처리되지 않은 나머지 모든 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex) {
        log.error("알 수 없는 서버 오류 발생: {}", ex.getMessage(), ex); // 전체 스택 트레이스 로깅
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "서버 내부 처리 중 오류가 발생했습니다. 관리자에게 문의해주세요."
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
    }
}