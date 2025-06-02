package com.lhl.minecraft_chat.backend;

import com.lhl.bconsole2.Screen;
import com.lhl.bconsole2.component.ArtText;
import com.lhl.bconsole2.component.ComponentFactory;
import com.lhl.bconsole2.component.Text;
import com.lhl.bconsole2.route.Router;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        ComponentFactory cf = new ComponentFactory();
        ArtText artText = cf.newArtText("Minecraft\nHistory");
        Text text = cf.newDivText("后端服务启动中...");
        artText.reg(text);
        Router.root.addChildRoute("starting",  artText);
        Screen.getScreen().turnON();

        SpringApplication.run(BackendApplication.class, args);
    }

}
