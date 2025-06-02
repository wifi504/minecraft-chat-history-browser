package com.lhl.minecraft_chat.backend.util;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/31_16:16
 */
public class PlayerAvatarGeneratorTest {

    @Test
    public void testGenerate() throws IOException {
        String playerName = "lhlnb";
        BufferedImage avatarUrl = PlayerAvatarGenerator.generate(playerName, 100);
        if (avatarUrl == null) throw new RuntimeException("生成头像失败");
        ImageIO.write(avatarUrl, "png", new File("avatar.png"));
    }
}
