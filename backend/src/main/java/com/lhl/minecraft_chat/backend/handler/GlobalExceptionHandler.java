package com.lhl.minecraft_chat.backend.handler;

import com.lhl.minecraft_chat.backend.result.R;
import com.lhl.minecraft_chat.backend.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/30_19:37
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理请求方法异常
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<?> handleMethodNotSupported() {
        return R.error(ResultCode.METHOD_NOT_ALLOWED);
    }

    // 处理404异常
    @ExceptionHandler(NoResourceFoundException.class)
    public R<?> handleNotFound() {
        return R.error(ResultCode.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public R<?> handlerException(Exception e) {
        e.printStackTrace();
        return R.error(ResultCode.SERVER_ERROR, "系统异常：" + e.getMessage().split(":")[0]);
    }
}
