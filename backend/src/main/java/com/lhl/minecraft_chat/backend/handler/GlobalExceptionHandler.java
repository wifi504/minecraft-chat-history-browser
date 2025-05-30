package com.lhl.minecraft_chat.backend.handler;

import com.lhl.minecraft_chat.backend.result.R;
import com.lhl.minecraft_chat.backend.result.ResultCode;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/30_19:37
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public R<?> handlerException(Exception e) {
        e.printStackTrace();
        return R.error(ResultCode.SERVER_ERROR, "系统异常：" + e.getMessage().split(":")[0]);
    }
}
