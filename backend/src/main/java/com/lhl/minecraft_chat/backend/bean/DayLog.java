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
        List<Message> messages = new ArrayList<>();
        HashMap<String, String> playerUUIDs = new HashMap<>();
        for (String line : lines) {
            // 匹配玩家UUID信息
            Matcher uuidMatcher = LogParsingUtil.PLAYER_UUID_PATTERN.matcher(line);
            if (uuidMatcher.find()) {
                String playerName = uuidMatcher.group(1);
                String playerUUID = uuidMatcher.group(2);
                playerUUIDs.put(playerName, playerUUID);
            }
            // 匹配聊天消息
            Matcher messageMatcher = LogParsingUtil.MESSAGE_PATTERN.matcher(line);
            if (messageMatcher.matches()) {
                Message message = Message.builder().timestamp(LogParsingUtil.getTimestamp(date + ' ' + messageMatcher.group(1))).username(messageMatcher.group(2)).content(messageMatcher.group(3)).build();
                messages.add(message);
            }
        }
        // 填充玩家头像
        for (Message message : messages) {
            String uuid = playerUUIDs.get(message.getUsername());
            if (uuid == null) continue;
            message.setPlayerUUID(uuid);
        }
        return messages;
    }
}
