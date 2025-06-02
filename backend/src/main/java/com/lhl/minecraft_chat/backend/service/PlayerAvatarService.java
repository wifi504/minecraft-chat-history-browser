package com.lhl.minecraft_chat.backend.service;

import com.lhl.minecraft_chat.backend.service.exception.PlayerAvatarServiceException;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/31_19:16
 */
@Service
public interface PlayerAvatarService {

    BufferedImage generate(String playerName, String size) throws PlayerAvatarServiceException;
}
