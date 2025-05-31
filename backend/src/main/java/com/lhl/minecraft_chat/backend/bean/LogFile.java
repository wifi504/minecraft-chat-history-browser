package com.lhl.minecraft_chat.backend.bean;

import lombok.Builder;
import lombok.Data;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/30_22:01
 */
@Data
@Builder
public class LogFile {
    private String fileName;
    private StringBuilder content;
}