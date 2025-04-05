package com.li.chat.netty.autoconfigure.socketio;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author malaka
 */
@ConfigurationProperties("friend-chat.socket.io")
@Getter
@Setter
public class SocketIoProperties {

    private int port;

    private int maxFramePayloadLength;

    private int maxHttpContentLength;

    private int workThreads;

    private boolean allowCustomRequests;

    private int upgradeTimeout;

    private int pingTimeout;

    private int pingInterval;

    private String nodeId;

}
