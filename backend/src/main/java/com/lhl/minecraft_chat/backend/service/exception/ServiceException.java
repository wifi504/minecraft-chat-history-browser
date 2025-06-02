package com.lhl.minecraft_chat.backend.service.exception;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/31_19:35
 */
public class ServiceException extends Exception {
    public ServiceException(String message) {
        super(message);
    }
}
