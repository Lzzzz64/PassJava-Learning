package org.example.passjava.question.exception;

import org.example.passjava.question.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // DTO 参数校验失败
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidation(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("请求参数不合法");

        return ResponseEntity.badRequest()
                .body(Result.error(400, message));
    }

    // URL 参数类型不正确
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex) {

        return ResponseEntity.badRequest()
                .body(Result.error(400, "请求参数类型错误"));
    }

    // Service 主动抛出的业务 HTTP 异常
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Result<Void>> handleStatus(
            ResponseStatusException ex) {

        int status = ex.getStatusCode().value();

        return ResponseEntity.status(ex.getStatusCode())
                .body(Result.error(
                        status,
                        ex.getReason() == null
                                ? "请求处理失败"
                                : ex.getReason()
                ));
    }

    // 其他未预料到的异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(
            Exception ex) {

        // 生产环境应通过日志框架记录异常详情，
        // 不要把堆栈信息直接返回给客户端。
        return ResponseEntity.status(
                        HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, "服务器内部错误"));
    }
}