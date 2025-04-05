package com.li.chat.netty.service;

import com.li.chat.common.enums.MessageStatusEnum;
import com.li.chat.domain.DTO.message.ChatMsgDTO;
import com.li.chat.netty.vo.PushBodyVo;
import com.li.chat.netty.vo.SendGroupVo;
import com.li.chat.netty.vo.SendVo;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author malaka
 */
public interface MessageService {

    int saveOfflineMsg(Long toId, ChatMsgDTO message);

    List<ChatMsgDTO> getOfflineMsgByUid(Long userId, Date date, long start, long end);

    void removeMsgByDate(Long userId, Date date);

    public PushBodyVo buildPushBody(ChatMsgDTO message);

    MessageStatusEnum sendSingleMessage(ChatMsgDTO message);

    ChatMsgDTO buildSingleMessageDTO(SendVo sendVo);

    ChatMsgDTO buildGroupMessageDTO(SendGroupVo sendVo);

    MessageStatusEnum sendGroupMessage(ChatMsgDTO message);

    Set<Long> sendGroupOnlineMessage(ChatMsgDTO message);
}
