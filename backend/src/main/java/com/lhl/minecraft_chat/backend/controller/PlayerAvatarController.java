package com.lhl.minecraft_chat.backend.controller;

import com.lhl.minecraft_chat.backend.result.R;
import com.lhl.minecraft_chat.backend.result.ResultCode;
import com.lhl.minecraft_chat.backend.service.PlayerAvatarService;
import com.lhl.minecraft_chat.backend.service.exception.PlayerAvatarServiceException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/31_19:09
 */
@RestController
@RequestMapping("/api/player-avatar")
public class PlayerAvatarController {

    @Autowired
    PlayerAvatarService playerAvatarService;

    @GetMapping("/generate/{playerName}")
    public void generate(@PathVariable String playerName,
                         @RequestParam(value = "size", defaultValue = "1000") String size,
                         HttpServletResponse response)
            throws Exception {
        BufferedImage image = playerAvatarService.generate(playerName, size);
        response.setContentType("image/png");
        ImageIO.write(image, "png", response.getOutputStream());
    }

    @GetMapping("/generate-base64/{playerName}")
    public R<?> generateBase64(@PathVariable String playerName,
                               @RequestParam(value = "size", defaultValue = "1000") String size) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            BufferedImage image = playerAvatarService.generate(playerName, size);
            ImageIO.write(image, "png", baos);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            return R.ok("data:image/png;base64," + base64);
        } catch (Exception e) {
            return R.error(ResultCode.SERVER_ERROR, e.getMessage());
        }
    }
}
