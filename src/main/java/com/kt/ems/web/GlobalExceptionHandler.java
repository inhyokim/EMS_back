package com.kt.ems.web;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex) {
        var fields = new ArrayList<Map<String,String>>();
        ex.getBindingResult().getFieldErrors().forEach(f -> 
            fields.add(Map.of("field", f.getField(), "message", f.getDefaultMessage())));
        return ResponseEntity.badRequest().body(Map.of("error", "validation_failed", "fields", fields));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
    
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        // Primary Key 중복 오류를 더 친화적으로 처리
        if (ex.getMessage() != null && ex.getMessage().contains("PRIMARY KEY")) {
            return ResponseEntity.badRequest().body(Map.of("error", "중복된 데이터입니다. 다른 값을 입력해주세요."));
        }
        // 외래키 제약조건 위반 (삭제 시)
        if (ex.getMessage() != null && ex.getMessage().contains("FOREIGN KEY")) {
            return ResponseEntity.badRequest().body(Map.of("error", "이 센서에 연결된 측정값이 있어서 삭제할 수 없습니다. 먼저 측정값을 삭제해주세요."));
        }
        return ResponseEntity.badRequest().body(Map.of("error", "데이터 무결성 오류가 발생했습니다."));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleOther(Exception ex) {
        return ResponseEntity.status(500).body(Map.of("error", "internal_error", "message", ex.getMessage()));
    }
}
