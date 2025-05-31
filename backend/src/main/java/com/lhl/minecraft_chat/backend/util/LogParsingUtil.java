package com.lhl.minecraft_chat.backend.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * 日志解析工具
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/30_19:26
 */
public class LogParsingUtil {

    private static final Charset CHARSET = Charset.forName("GBK");

    // 预编译的提取消息的正则表达式
    public static final Pattern MESSAGE_PATTERN =
            Pattern.compile("^\\[(.{8})\\] \\[Server thread/INFO\\]: <(.+?)> (.+)$");
    // 预编译的提取玩家UUID的正则表达式
    public static final Pattern PLAYER_UUID_PATTERN =
            Pattern.compile("UUID of player (.+) is (.+)");

    /**
     * 解析日志文件（自动处理.gz和.log）
     *
     * @param logFile 上传的日志文件
     * @return 包含文件内容的StringBuilder
     * @throws IOException 文件读取异常
     */
    public static StringBuilder parseLog(MultipartFile logFile) throws IOException {
        if (logFile == null || logFile.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String filename = logFile.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("无效的文件名");
        }

        // 根据文件类型选择处理方式
        if (filename.endsWith(".gz")) {
            return decompressGzip(logFile.getInputStream());
        } else if (filename.endsWith(".log")) {
            return new StringBuilder(new String(logFile.getBytes(), CHARSET));
        } else {
            throw new IllegalArgumentException("不支持的文件类型，仅支持.log和.gz");
        }
    }

    /**
     * 解压GZIP文件
     *
     * @param gzippedInputStream GZIP文件输入流
     * @return 解压后的内容
     * @throws IOException 解压异常
     */
    private static StringBuilder decompressGzip(InputStream gzippedInputStream) throws IOException {
        StringBuilder content = new StringBuilder();
        try (GZIPInputStream gzipIn = new GZIPInputStream(gzippedInputStream);
             BufferedReader reader = new BufferedReader(new InputStreamReader(gzipIn, CHARSET))) {

            char[] buffer = new char[8192];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                content.append(buffer, 0, charsRead);
            }
        }
        return content;
    }

    /**
     * 获取时间戳
     *
     * @param dateTime "yyyy-MM-dd HH:mm:ss"
     * @return 时间戳
     */
    public static Long getTimestamp(String dateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, dateTimeFormatter);
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
