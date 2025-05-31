package com.lhl.minecraft_chat.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/31_15:56
 */
@Data
@Configuration
@ConfigurationProperties (prefix = "log-file")
public class LogFileConfig {
    private String path;
}
