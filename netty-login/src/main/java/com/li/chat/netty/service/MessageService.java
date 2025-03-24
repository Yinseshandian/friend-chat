package com.li.chat.netty.service;

import com.li.chat.domain.DTO.message.ChatMsgDTO;

import java.util.Date;
import java.util.List;

/**
 * @author malaka
 */
public interface MessageService {

    int saveOfflineMsg(Long toId, ChatMsgDTO message);

    List<ChatMsgDTO> getOfflineMsgByUid(Long userId, Date date, long start, long end);

    void removeMsg(Long userId, Date date);


}
