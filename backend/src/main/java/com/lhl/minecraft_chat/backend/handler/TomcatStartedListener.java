package com.lhl.minecraft_chat.backend.handler;

import com.lhl.bconsole2.Screen;
import com.lhl.bconsole2.component.ComponentFactory;
import com.lhl.bconsole2.component.Text;
import com.lhl.bconsole2.component.View;
import com.lhl.bconsole2.component.preset.PresetViewsFactory;
import com.lhl.bconsole2.route.Router;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author WIFI连接超时
 * @version 1.0
 * Create Time 2025/6/2_0:17
 */
@Component
public class TomcatStartedListener implements ApplicationListener<WebServerInitializedEvent> {
    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        ComponentFactory cf = new ComponentFactory();
        Text text = cf.newDivText("""
                Minecraft 聊天记录浏览器
                                
                后端服务启动完成，请访问：
                http://localhost:25566/

                本项目基于协议 GNU General Public License v3.0 开源，前往 Github：
                https://github.com/wifi504/minecraft-chat-history-browser""");

        Router.root.addChildRoute("started", text);

        PresetViewsFactory pvf = new PresetViewsFactory();
        View clock = pvf.newClockView("Minecraft 聊天记录浏览器");
        Router.root.addChildRoute("clock", clock);

        while (true) {
            Router.root.navigate("started");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignore) {
            }
            Screen.getScreen().getUserInput(cf.newDivText("按 Enter 键进入屏保时钟..."));
            Router.root.navigate("clock");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignore) {
            }
            Screen.getScreen().waitUserInterrupt(cf.newDivText("按 Enter 键返回..."));
        }
    }
}
