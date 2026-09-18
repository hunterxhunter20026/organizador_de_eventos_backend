package com.organizadoreventos.infrastructure.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

/** ARCHITECTURAL TRACE: Infrastructure Layer — traduce excepciones de dominio a HTTP */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> manejarArgumentoInvalido(IllegalArgumentException ex) {
        return construir(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException ex) {
        String detalle = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return construir(HttpStatus.BAD_REQUEST, detalle);
    }

    private ResponseEntity<Map<String, Object>> construir(HttpStatus status, String mensaje) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now().toString(), "status", status.value(), "mensaje", mensaje));
    }
}
