package com.lhl.minecraft_chat.backend.bean;

import com.lhl.minecraft_chat.backend.util.LogParsingUtil;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/30_22:04
 */
@Data
@Builder
public class DayLog {
    private String date;
    private TreeMap<Integer, StringBuilder> content;

    public List<Message> resolveMessages() {
        if (content == null || content.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        content.forEach((key, value) -> sb.append(value));
        String[] lines = sb.toString().split("\\r?\\n");
        return LogParsingUtil.resolveMessages(lines, date);
    }
}
