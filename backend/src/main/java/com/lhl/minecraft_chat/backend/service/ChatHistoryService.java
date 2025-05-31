package com.lhl.minecraft_chat.backend.service;

import com.lhl.minecraft_chat.backend.bean.Message;
import com.lhl.minecraft_chat.backend.service.exception.ChatHistoryServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/30_19:31
 */
@Service
public interface ChatHistoryService {

    List<Message> resolveLogFiles(List<MultipartFile> files) throws ChatHistoryServiceException;
}
