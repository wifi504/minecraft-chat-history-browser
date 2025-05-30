package com.lhl.minecraft_chat.backend.controller;

import com.lhl.minecraft_chat.backend.result.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/30_19:30
 */
@RestController
@RequestMapping("/api/chat-history")
public class ChatHistoryController {

    @GetMapping("/list")
    public R<?> list() {
        return R.ok("获取聊天记录成功");
    }
}
