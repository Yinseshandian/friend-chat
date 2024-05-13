package com.li.chat.netty.config;

import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author malaka
 */
@Configuration
public class SocketIoConfig {
    /*
      prot: 10100
      maxFramePayloadLength: 1048576
      maxHttpContentLength: 1048576
      workThreads: 1048576
     */

    @Value("${socket.io.port:10100}")
    private int port;

    @Value("${socket.io.maxFramePayloadLength}")
    private int maxFramePayloadLength;

    @Value("${socket.io.maxHttpContentLength}")
    private int maxHttpContentLength;

    @Value("${socket.io.workThreads}")
    private int workThreads;

    @Value("${socket.io.allowCustomRequests}")
    private boolean allowCustomRequests;

    @Value("${socket.io.upgradeTimeout}")
    private int upgradeTimeout;

    @Value("${socket.io.pingTimeout}")
    private int pingTimeout;

    @Value("${socket.io.pingInterval}")
    private int pingInterval;



    @Bean("socketIOServer")
    public SocketIOServer socketIOServer() {

        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setPort(port);
        com.corundumstudio.socketio.SocketConfig socketConfig = new com.corundumstudio.socketio.SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);
        config.setWorkerThreads(workThreads);
        config.setAllowCustomRequests(allowCustomRequests);
        config.setUpgradeTimeout(upgradeTimeout);
        config.setPingTimeout(pingTimeout);
        config.setPingInterval(pingInterval);
        config.setMaxHttpContentLength(maxHttpContentLength);
        config.setMaxFramePayloadLength(maxFramePayloadLength);
        config.setTransports(Transport.WEBSOCKET);//指定传输协议为WebSocket
        SocketIOServer socketIOServer = new SocketIOServer(config);
        socketIOServer.start();
        return socketIOServer;

    }

    /**
     * 注入OnConnect，OnDisconnect，OnEvent注解。 不写的话Spring无法扫描OnConnect，OnDisconnect等注解
     * */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketIOServer){
        return new SpringAnnotationScanner(socketIOServer);
    }

}
