package com.li.chat.netty.service;

import com.li.chat.common.enums.MessageStatusEnum;
import com.li.chat.domain.DTO.message.MessageDTO;
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

    int saveOfflineMsg(Long toId, MessageDTO message);

    List<MessageDTO> getOfflineMsgByUid(Long userId, Date date, long start, long end);

    void removeMsgByDate(Long userId, Date date);

    public PushBodyVo buildPushBody(MessageDTO message);

    MessageStatusEnum sendSingleMessage(MessageDTO message);

    MessageDTO buildSingleMessageDTO(SendVo sendVo);

    MessageDTO buildGroupMessageDTO(SendGroupVo sendVo);

    MessageStatusEnum sendGroupMessage(MessageDTO message);

    Set<Long> sendGroupOnlineMessage(MessageDTO message);
}
