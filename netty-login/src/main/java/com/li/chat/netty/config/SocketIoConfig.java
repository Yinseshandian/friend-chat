package com.li.chat.netty.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.rsocket.server.RSocketServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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



   /* @Bean("socketIOServer")
    public SocketIOServer socketIOServer() {

        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setPort(port);
        com.corundumstudio.socketio.SocketConfig socketConfig = new com.corundumstudio.socketio.SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);
        config.setOrigin(null);
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

    }*/

    @Bean("socketIOServer")
     public SocketIOServer socketioServer() {
         com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
         // 配置域名和端口
         config.setPort(port);
         // 开启socket端口复用
         com.corundumstudio.socketio.SocketConfig socketConfig = new com.corundumstudio.socketio.SocketConfig();
         socketConfig.setReuseAddress(true);
         config.setSocketConfig(socketConfig);
         // 连接数大小
         config.setWorkerThreads(workThreads);
         // 允许客户请求
         config.setAllowCustomRequests(allowCustomRequests);
         // 协议升级超时时间(毫秒)，默认10秒，HTTP握手升级为ws协议超时时间
         config.setUpgradeTimeout(upgradeTimeout);
         // Ping消息超时时间(毫秒)，默认60秒，这个时间间隔内没有接收到心跳消息就会发送超时事件
         config.setPingTimeout(pingTimeout);
         // Ping消息间隔(毫秒)，默认25秒。客户端向服务器发送一条心跳消息间隔
         config.setPingInterval(pingInterval);
         // 设置HTTP交互最大内容长度
         config.setMaxFramePayloadLength(maxFramePayloadLength);
         // 设置最大每帧处理数据的长度，防止他人利用大数据来攻击服务器
         config.setMaxHttpContentLength(maxHttpContentLength);
         config.setOrigin(null);

         SocketIOServer server = new SocketIOServer(config);

         return server;
     }

     /**
      * 注入OnConnect，OnDisconnect，OnEvent注解。 不写的话Spring无法扫描OnConnect，OnDisconnect等注解
      * */
    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketIOServer){
        return new SpringAnnotationScanner(socketIOServer);
    }

}
