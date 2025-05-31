package com.lhl.minecraft_chat.backend.controller;

import com.lhl.minecraft_chat.backend.bean.LogFile;
import com.lhl.minecraft_chat.backend.bean.Message;
import com.lhl.minecraft_chat.backend.result.R;
import com.lhl.minecraft_chat.backend.result.ResultCode;
import com.lhl.minecraft_chat.backend.service.ChatHistoryService;
import com.lhl.minecraft_chat.backend.service.exception.ChatHistoryServiceException;
import com.lhl.minecraft_chat.backend.util.LogParsingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/30_19:30
 */
@RestController
@RequestMapping("/api/chat-history")
public class ChatHistoryController {

    @Autowired
    private ChatHistoryService chatHistoryService;

    @GetMapping("/list")
    public R<?> list() {
        return R.ok("获取聊天记录成功".repeat(100));
    }

    @PostMapping("/upload")
    public R<?> upload(@RequestParam("files") List<MultipartFile> files) {
        try {
            return R.ok(chatHistoryService.resolveLogFiles(files));
        } catch (ChatHistoryServiceException e) {
            return R.error(ResultCode.FAIL, e.getMessage());
        }
    }
}
