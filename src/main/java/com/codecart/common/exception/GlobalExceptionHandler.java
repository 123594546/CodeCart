package com.codecart.common.exception;

import com.codecart.common.result.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ApiResult<Void> handleBizException(BizException exception) {
        return ApiResult.fail(400, exception.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ApiResult<Void> handleUnauthorizedException(UnauthorizedException exception) {
        return ApiResult.fail(401, exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("请求参数校验失败");
        return ApiResult.fail(400, message);
    }

    @ExceptionHandler(Exception.class)
    public ApiResult<Void> handleException(Exception exception) {
        log.error("Unhandled exception", exception);
        return ApiResult.fail("系统异常，请稍后重试");
    }
}
