package io.github.com.crud_pessoa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException; // IMPORTANTE: Adicione esta importação

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Handler para erros de validação (@NotBlank, @Size, @Pattern, @Valid em DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value()); // 400
        response.put("error", "Bad Request");
        response.put("message", "Validation failed for one or more fields.");
        response.put("details", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Handler para erros de leitura da mensagem HTTP (JSON malformado ou tipos de dados incorretos)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex) {

        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.BAD_REQUEST.value()); // 400
        response.put("error", "Bad Request");
        response.put("message", "Malformed JSON request or invalid data type. Please check your request body syntax and data types.");
        // Opcional: Para depuração em desenvolvimento, você pode incluir ex.getLocalizedMessage()
        // response.put("details", ex.getLocalizedMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CpfAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleCpfConflict(CpfAlreadyExistsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 409);
        response.put("error", "Data Conflict");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(CpfMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleCpfMismatchException(CpfMismatchException ex) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", HttpStatus.BAD_REQUEST.value());
                response.put("error", "CPF Mismatch Error");
                response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.NOT_FOUND.value()); // 404
        response.put("error", "Not Found");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 500);
        response.put("error", "Internal Server Error");
        response.put("message", "An unexpected error occurred."); // Mensagem genérica para segurança em produção
        // Importante: Em um ambiente real, você deve logar a exceção completa aqui
        System.err.println("Unexpected error: " + ex.getMessage()); // Exemplo de log simples
        // ex.printStackTrace(); // Apenas para depuração, remova em produção

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}