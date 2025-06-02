package com.lhl.minecraft_chat.backend.util;

import com.lhl.minecraft_chat.backend.bean.Message;
import com.lhl.minecraft_chat.backend.config.LogFileConfig;
import org.mozilla.universalchardet.UniversalDetector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * 日志解析工具
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/30_19:26
 */
@Component
public class LogParsingUtil {

    private static List<String> filter;

    @Autowired
    public LogParsingUtil(LogFileConfig config) {
        filter = config.getFilter();
    }

    /**
     * 解析 MultipartFile 日志文件（自动识别编码 + 支持 .gz/.log）
     */
    public static StringBuilder parseLog(MultipartFile logFile) throws IOException {
        if (logFile == null || logFile.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String filename = logFile.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("无效的文件名");
        }

        byte[] fileBytes = logFile.getBytes();

        if (filename.endsWith(".gz")) {
            // 解压 .gz 后再识别编码
            byte[] uncompressedBytes = decompressGzipToBytes(fileBytes);
            Charset detected = detectCharset(new ByteArrayInputStream(uncompressedBytes));
            return new StringBuilder(new String(uncompressedBytes, detected));

        } else if (filename.endsWith(".log")) {
            // 直接识别编码
            Charset detected = detectCharset(new ByteArrayInputStream(fileBytes));
            return new StringBuilder(new String(fileBytes, detected));

        } else {
            throw new IllegalArgumentException("不支持的文件类型，仅支持 .log 和 .gz");
        }
    }

    /**
     * 解压 GZIP 为原始字节数组（不解码）
     */
    private static byte[] decompressGzipToBytes(byte[] gzData) throws IOException {
        try (GZIPInputStream gzipIn = new GZIPInputStream(new ByteArrayInputStream(gzData));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8192];
            int len;
            while ((len = gzipIn.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        }
    }

    /**
     * 自动识别编码（使用 juniversalchardet）
     */
    private static Charset detectCharset(InputStream input) throws IOException {
        byte[] buf = input.readAllBytes();
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(buf, 0, buf.length);
        detector.dataEnd();

        String encoding = detector.getDetectedCharset();
        detector.reset();

        return encoding != null ? Charset.forName(encoding) : Charset.defaultCharset();
    }

    /**
     * 转换时间为时间戳
     */
    public static Long getTimestamp(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatter);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    // 预编译正则表达式
    // ---- 日志行提取日志消息 ----
    // 信息级别日志 group(1)时间 group(2)日志消息
    private static final Pattern INFO_LOG_PATTERN =
            Pattern.compile("\\[(\\d{2}:\\d{2}:\\d{2})\\] \\[Server thread/INFO\\]: (.+)");
    // 警告级别日志 group(1)时间 group(2)日志消息
    private static final Pattern WARN_LOG_PATTERN =
            Pattern.compile("\\[(\\d{2}:\\d{2}:\\d{2})\\] \\[Server thread/WARN\\]: (.+)");
    // ---- 日志消息提取日志内容 ----
    // 玩家聊天内容 group(1)玩家名 group(2)聊天消息
    private static final Pattern PLAYERCHAT_MSG_PATTERN =
            Pattern.compile("^<([^>]+)> (.+)$");
    // 玩家登入登出 group(1)玩家名 group(2)joined或left
    private static final Pattern PLAYERLOGINOUT_MSG_PATTERN =
            Pattern.compile("^(.+?) (joined|left) the game$");

    /**
     * 从日志行解析日志消息
     *
     * @param lines 日志行数组
     * @param date  日志日期 yyyy-MM-dd
     * @return 日志消息列表（时间戳排序）
     */
    public static List<Message> resolveMessages(String[] lines, String date) {
        List<Message> messages = new ArrayList<>();
        for (String line : lines) {
            Message.MessageBuilder messageBuilder = Message.builder();
            Matcher infoLogMatcher = INFO_LOG_PATTERN.matcher(line);
            Matcher warnLogMatcher = WARN_LOG_PATTERN.matcher(line);
            if (infoLogMatcher.find()) {
                // 信息级别日志
                messageBuilder.timestamp(getTimestamp(date + ' ' + infoLogMatcher.group(1)));
                String logMsg = infoLogMatcher.group(2);
                Matcher playerchatMsgMatcher = PLAYERCHAT_MSG_PATTERN.matcher(logMsg);
                Matcher playerloginoutMsgMatcher = PLAYERLOGINOUT_MSG_PATTERN.matcher(logMsg);
                if (playerchatMsgMatcher.find()) {
                    // 玩家聊天内容
                    messageBuilder.type("PLAYER-CHAT-MSG")
                            .username(playerchatMsgMatcher.group(1))
                            .content(playerchatMsgMatcher.group(2));
                    if (filter.contains("PLAYER-CHAT-MSG")) {
                        messages.add(messageBuilder.build());
                    }
                } else if (playerloginoutMsgMatcher.find()) {
                    // 玩家登入登出
                    messageBuilder.type("PLAYER-LOGINOUT-MSG")
                            .username(playerloginoutMsgMatcher.group(1));
                    String type = playerloginoutMsgMatcher.group(2);
                    if ("joined".equals(type)) {
                        messageBuilder.content("加入了游戏");
                    } else {
                        messageBuilder.content("离开了游戏");
                    }
                    if (filter.contains("PLAYER-LOGINOUT-MSG")) {
                        messages.add(messageBuilder.build());
                    }
                } else {
                    // 其他信息
                    messageBuilder.type("RAW-INFO-LOG").content(logMsg);
                    if (filter.contains("RAW-INFO-LOG")) {
                        messages.add(messageBuilder.build());
                    }
                }
            } else if (warnLogMatcher.find()) {
                // 警告级别日志
                messageBuilder.timestamp(getTimestamp(date + ' ' + warnLogMatcher.group(1)))
                        .type("RAW-WARN-LOG")
                        .content(warnLogMatcher.group(2));
                if (filter.contains("RAW-WARN-LOG")) {
                    messages.add(messageBuilder.build());
                }
            }
        }
        messages.sort(Comparator.comparingLong(Message::getTimestamp));
        return messages;
    }
}