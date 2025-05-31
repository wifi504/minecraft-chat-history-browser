package com.lhl.minecraft_chat.backend.service.impl;

import com.lhl.minecraft_chat.backend.bean.DayLog;
import com.lhl.minecraft_chat.backend.bean.LogFile;
import com.lhl.minecraft_chat.backend.bean.Message;
import com.lhl.minecraft_chat.backend.service.ChatHistoryService;
import com.lhl.minecraft_chat.backend.service.exception.ChatHistoryServiceException;
import com.lhl.minecraft_chat.backend.util.LogParsingUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/30_21:32
 */
@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {
    @Override
    public List<Message> resolveLogFiles(List<MultipartFile> files) throws ChatHistoryServiceException {
        // 1. 参数校验
        if (files == null || files.isEmpty()) {
            throw new ChatHistoryServiceException("请选择文件");
        }
        // 2. 把前端传过来的文件全部解析成 LogFile
        int successCount = 0;
        List<LogFile> logFiles = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                LogFile logFile = LogFile.builder()
                        .fileName(file.getOriginalFilename())
                        .content(LogParsingUtil.parseLog(file))
                        .build();
                logFiles.add(logFile);
                successCount++;
            }
        } catch (IOException e) {
            throw new ChatHistoryServiceException("文件处理失败：" + e.getMessage());
        }
        if (successCount != files.size()) {
            throw new ChatHistoryServiceException(String.format("部分文件处理成功，失败: %d 个", files.size() - successCount));
        }
        // 3. 把 LogFile 的 List 集合转换成 DayLog 的 List
        List<DayLog> dayLogs = new ArrayList<>();
        for (LogFile logFile : logFiles) {
            String fileName = logFile.getFileName().substring(0, logFile.getFileName().indexOf("."));
            String date;
            int index;
            if ("latest".equals(fileName)) {
                date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                index = Integer.MAX_VALUE;
            } else {
                date = fileName.substring(0, 10);
                index = Integer.parseInt(fileName.substring(11));
            }
            Optional<DayLog> any = dayLogs.stream().filter(dayLog -> Objects.equals(dayLog.getDate(), date)).findAny();
            if (any.isPresent()) {
                any.get().getContent().put(index, logFile.getContent());
            } else {
                TreeMap<Integer, StringBuilder> map = new TreeMap<>();
                map.put(index, logFile.getContent());
                dayLogs.add(DayLog.builder()
                        .date(date)
                        .content(map)
                        .build());
            }
        }
        // 4. DayLog 进行日期排序
        dayLogs.sort(Comparator.comparing(DayLog::getDate));
        // 5. 得到 DayLog 的所有 Message
        List<Message> messages = new ArrayList<>();
        for (DayLog dayLog : dayLogs) {
            messages.addAll(dayLog.resolveMessages());
        }
        return messages;
    }
}
