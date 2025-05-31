package com.lhl.minecraft_chat.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/30_20:24
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    // 允许哪些源访问
    private List<String> allowedOrigins = Collections.emptyList();
    // 允许哪些方法
    private List<String> allowedMethods = Collections.emptyList();
    // 允许携带哪些头部信息
    private List<String> allowedHeaders = Collections.emptyList();
    // 允许携带凭证（如果需要）
    private Boolean allowCredentials = false;
}
