package com.lhl.minecraft_chat.backend.util;

import org.junit.jupiter.api.Test;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/31_16:16
 */
public class PlayerAvatarGeneratorTest {

    @Test
    public void testGenerate() {
        String playerName = "lhlnb";
        String avatarUrl = PlayerAvatarGenerator.generate(playerName, 1000);
        System.out.println(avatarUrl);
    }
}
