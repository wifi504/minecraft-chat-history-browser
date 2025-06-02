package com.lhl.minecraft_chat.backend.service.impl;

import com.lhl.minecraft_chat.backend.service.PlayerAvatarService;
import com.lhl.minecraft_chat.backend.service.exception.PlayerAvatarServiceException;
import com.lhl.minecraft_chat.backend.util.PlayerAvatarGenerator;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/31_19:17
 */
@Service
public class PlayerAvatarServiceImpl implements PlayerAvatarService {
    @Override
    public BufferedImage generate(String playerName, String size) throws PlayerAvatarServiceException {
        BufferedImage avatar;
        try {
            avatar = PlayerAvatarGenerator.generate(playerName, Integer.parseInt(size));
        } catch (NumberFormatException e) {
            throw new PlayerAvatarServiceException("size参数错误");
        }
        if (avatar == null) throw new PlayerAvatarServiceException("生成头像失败");
        return avatar;
    }
}
