package com.lhl.minecraft_chat.backend.bean;

import lombok.Builder;
import lombok.Data;

/**
 * 日志消息实体类
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/30_21:23
 */
@Data
@Builder
public class Message {
    private Long timestamp;
    private String type;
    private String username;
    private String content;
}
