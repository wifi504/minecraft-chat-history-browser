package com.lhl.minecraft_chat.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhl.minecraft_chat.backend.dto.MojangUserProfileDto;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Base64;

/**
 * 根据玩家名称生成玩家头像 Base64
 *
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/5/31_16:07
 */
public class PlayerAvatarGenerator {

    /**
     * 生成玩家头像
     *
     * @param playerName 玩家名字
     * @param size       头像大小
     * @return Base64
     */
    public static String generate(String playerName, int size) {
        try {
            // 1. 从麻将官方解析玩家UUID
            String playerUUID = getPlayerUUIDFromMojang(playerName);
            // 2. 从 crafatar.com 下载玩家皮肤
            byte[] skinImage = downloadSkinFromCrafatar(playerUUID);
            // 3. 将皮肤裁剪拼接成新头像
            BufferedImage avatar = generateHead(ImageIO.read(new ByteArrayInputStream(skinImage)));
            // 4. 缩放
            avatar = resize(avatar, size, size);
            // 5. 将头像转为 Base64
            // ImageIO.write(avatar, "png", new File("avatar.png"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(avatar, "png", baos);
            byte[] avatarBytes = baos.toByteArray();
            baos.close();
            return Base64.getEncoder().encodeToString(avatarBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从麻将官方解析玩家UUID
     *
     * @param playerName 玩家名字
     * @return UUID
     */
    private static String getPlayerUUIDFromMojang(String playerName) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + playerName))
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        MojangUserProfileDto userProfile = new ObjectMapper().readValue(response.body(), MojangUserProfileDto.class);
        return userProfile.getId();
    }


    /**
     * 从 Crafatar下载玩家皮肤
     */
    private static byte[] downloadSkinFromCrafatar(String uuid) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://crafatar.com/skins/" + uuid))
                .GET()
                .build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        return response.body();
    }

    // 将皮肤裁剪拼接成新头像
    // 参阅 https://github.com/MCCAG/Backend/blob/main/Scripts/Renderer.py
    private static BufferedImage generateHead(BufferedImage skin) {

        int canvasSize = 1000;
        int headSize = 850;
        int helmetSize = 934;

        int width = skin.getWidth();
        int height = skin.getHeight();

        if (!(width == 64 && (height == 32 || height == 64))) {
            throw new IllegalArgumentException("Invalid skin size: " + width + "x" + height);
        }

        BufferedImage canvas = new BufferedImage(canvasSize, canvasSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        BufferedImage face = skin.getSubimage(8, 8, 8, 8);
        BufferedImage scaledFace = new BufferedImage(headSize, headSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D faceG = scaledFace.createGraphics();
        faceG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        faceG.drawImage(face, 0, 0, headSize, headSize, null);
        faceG.dispose();
        g.drawImage(scaledFace, (canvasSize - headSize) / 2, (canvasSize - headSize) / 2, null);

        if (height == 64) {
            BufferedImage helmet = skin.getSubimage(40, 8, 8, 8);
            BufferedImage scaledHelmet = new BufferedImage(helmetSize, helmetSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D helmetG = scaledHelmet.createGraphics();
            helmetG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            helmetG.drawImage(helmet, 0, 0, helmetSize, helmetSize, null);
            helmetG.dispose();

            // 创建阴影图层
            BufferedImage shadow = new BufferedImage(helmetSize, helmetSize, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < helmetSize; y++) {
                for (int x = 0; x < helmetSize; x++) {
                    int argb = scaledHelmet.getRGB(x, y);
                    if ((argb >> 24) != 0x00) {
                        shadow.setRGB(x, y, new Color(0, 0, 0, 80).getRGB());
                    }
                }
            }

            // 模糊
            shadow = blurImage(shadow, 15);

            // 绘制阴影
            g.drawImage(shadow, (canvasSize - helmetSize) / 2, ((canvasSize - helmetSize) / 2), null);

            // 绘制头盔
            g.drawImage(scaledHelmet, (canvasSize - helmetSize) / 2, (canvasSize - helmetSize) / 2, null);
        }

        g.dispose();
        return canvas;
    }

    //  模糊处理
    private static BufferedImage blurImage(BufferedImage image, int size) {
        if (size % 2 == 0 || size < 3) {
            throw new IllegalArgumentException("Blur kernel size must be odd and >= 3");
        }
        int total = size * size;
        float[] kernel = new float[total];
        Arrays.fill(kernel, 1f / total);
        BufferedImageOp op = new ConvolveOp(new Kernel(size, size, kernel), ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, null);
    }

    // 缩放
    private static BufferedImage resize(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();

        // 像素风格设置
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();

        return resizedImage;
    }
}
