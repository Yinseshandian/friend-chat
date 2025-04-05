package com.li.chat.netty.listener;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.li.chat.common.enums.MessageClientEnum;
import com.li.chat.common.enums.MessageSocketioEvent;
import com.li.chat.common.enums.MessageStatusEnum;
import com.li.chat.domain.DTO.message.ChatMsgDTO;
import com.li.chat.netty.service.MessageService;
import com.li.chat.netty.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(0)
public class GroupEventListener extends AbstractEventListener{

    @Autowired
    private MessageService messageService;

    public static final String ROOM_GROUP_KEY = "room_group_";

    @OnEvent(MessageSocketioEvent.EVENT_SEND_GROUP_MESSAGE)
    public void onSendMessage(SocketIOClient client, AckRequest ackRequest, SendGroupVo sendVo) {
        SendStatusVo.SendStatusVoBuilder statusVoBuilder = SendStatusVo.builder();
        try{
            Long userId = client.get(MessageClientEnum.CLIENT_UID_KEY);
            if (userId == null) {
                sendAck(ackRequest, statusVoBuilder.status(MessageStatusEnum.USER_OFFLINE));
                return;
            }
            sendVo.setFromId(userId);
            ChatMsgDTO message = messageService.buildGroupMessageDTO(sendVo);

            MessageStatusEnum sendStatusEnum = messageService.sendGroupMessage(message);

            // 返回ACK给发送方
            sendAck(ackRequest, statusVoBuilder.msgId(message.getId()).status(sendStatusEnum).build());


        }catch (Exception e) {
            sendAck(ackRequest, statusVoBuilder.status(MessageStatusEnum.UNKNOWN_ERR).build());
        }

    }

    private void sendAck(AckRequest ackRequest, Object... data) {
        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData(data);
        }
    }

    public static String buildRoomKey(Object s) {
        return ROOM_GROUP_KEY + s;
    }

}