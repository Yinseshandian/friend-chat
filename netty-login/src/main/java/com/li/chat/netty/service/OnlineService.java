package com.li.chat.netty.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.li.chat.common.enums.ChatOnlineEnum;

/**
 * @author malaka
 */
public interface OnlineService {

    Long goOnline(String token,  SocketIOClient client);

    void goGroupOnline(SocketIOClient client);

    boolean goOffline( SocketIOClient client);

    ChatOnlineEnum checkOnline(Long userId);

    String getOnlineNodeId(Long userId);

}
