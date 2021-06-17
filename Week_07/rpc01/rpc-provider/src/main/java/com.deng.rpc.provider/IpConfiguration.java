package com.deng.rpc.provider;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 获取项目注册的端口号
 */
@Component
public class IpConfiguration implements ApplicationListener<WebServerInitializedEvent>{


    private int port;

    private static int serverPort;

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        port = webServerInitializedEvent.getWebServer().getPort();
        serverPort = port;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public int getPort(){
        return serverPort;
    }
}
