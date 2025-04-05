package com.li.chat.netty.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.li.chat.common.enums.ChatOnlineEnum;
import com.li.chat.domain.DTO.message.ChatMsgDTO;
import com.li.chat.netty.vo.SendGroupVo;
import com.li.chat.netty.vo.SendVo;

/**
 * @author malaka
 */
public interface OnlineService {

    Long goOnline(String token,  SocketIOClient client);

    void goGroupOnline(SocketIOClient client);

    boolean goOffline( SocketIOClient client);

    ChatOnlineEnum checkOnline(Long userId);


}
