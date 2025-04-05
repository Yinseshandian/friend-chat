package com.li.chat.netty.listener;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.li.chat.common.enums.MessageClientEnum;
import com.li.chat.common.enums.MessageSocketioEvent;
import com.li.chat.common.enums.MessageStatusEnum;
import com.li.chat.common.enums.RedisCachePrefixEnum;
import com.li.chat.domain.DTO.message.ChatMsgDTO;
import com.li.chat.netty.service.OnlineService;
import com.li.chat.netty.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@Order(1)
public class UserEventListener extends AbstractEventListener{


    @Autowired
    private OnlineService onlineService;

    @OnConnect
    public void eventOnConnect(SocketIOClient client) {
        Map<String, List<String>> urlParams = client.getHandshakeData().getUrlParams();
        log.info("客户端唯一标识为：{}", client.getSessionId());
        log.info("链接开启，urlParams：{}", urlParams);
    }

    @OnEvent(MessageSocketioEvent.EVENT_ONLINE)
    public void online(SocketIOClient client, AckRequest ackRequest, Map<String, String> data) {
        String token = data.get("token");
        HashMap<String, String> map = new HashMap<>();
        Long userId = onlineService.goOnline(token, client);
        if (userId == null) {
            map.put("code", "1");
            map.put("msg", "未登录");
            sendAck(ackRequest, map);
            return;
        }
        // 返回登录成功ACK
        map.put("code", "0");
        map.put("msg", "上线成功");
        sendAck(ackRequest, map);
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        Long userId = client.get(MessageClientEnum.CLIENT_UID_KEY);
        if (userId != null) {
            redisCache.deleteObject(RedisCachePrefixEnum.NETTY_CHAT_ONLINE + userId);
            log.info("用户下线: {}", userId);
            onlineService.goOffline(client);
        }
    }

    @OnEvent(MessageSocketioEvent.EVENT_SEND_MESSAGE)
    public void onSendMessage(SocketIOClient client, AckRequest ackRequest, SendVo sendVo) {
        SendStatusVo.SendStatusVoBuilder statusVoBuilder = SendStatusVo.builder();
        try{
            Long userId = client.get(MessageClientEnum.CLIENT_UID_KEY);
            if (userId == null) {
                sendAck(ackRequest, statusVoBuilder.status(MessageStatusEnum.USER_OFFLINE));
                return;
            }
            log.info("用户{}发送单聊消息，目标：{}，消息内容：{} {}", userId, sendVo.getUserId(), sendVo.getMsgType(), sendVo.getContent());
            sendVo.setFromId(userId);

            ChatMsgDTO message = messageService.buildSingleMessageDTO(sendVo);

            MessageStatusEnum sendStatusEnum = messageService.sendSingleMessage(message);

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

    @OnEvent(MessageSocketioEvent.EVENT_MESSAGE_ACK)
    public void onMessageAck(SocketIOClient client, Map<String, String> data) {
        String msgId = data.get("msgId");
        String status = data.get("status"); // delivered/read

    }

    @OnEvent(MessageSocketioEvent.EVENT_PULL_OFFLINE_MESSAGE)
    private void pullOfflineMessages(SocketIOClient client) {
        Long userId = client.get(MessageClientEnum.CLIENT_UID_KEY);
        if (userId == null) {
            return;
        }
        log.info("用户{}拉取离线消息", userId);
        Date currentDate = new Date();

        // 每次获取的消息数量
        int batchSize = 200;
        // 遍历前7天
        for (int i = 0; i < 7; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            Date targetDate = calendar.getTime();

            long start = 0;
            List<ChatMsgDTO> msgDTOList;

            // 分批获取当天的消息，直到没有更多消息
            do {
                msgDTOList = messageService.getOfflineMsgByUid(userId, targetDate, start, start + batchSize - 1);

                if (msgDTOList == null || msgDTOList.isEmpty()) {
                    break;
                }
                List<PushBodyVo> pushBodyVoList = new ArrayList<>();
                for (ChatMsgDTO chatMsgDTO : msgDTOList) {
                    pushBodyVoList.add(messageService.buildPushBody(chatMsgDTO));
                }
                client.sendEvent(MessageSocketioEvent.SEND_OFFLINE_MESSAGE, pushBodyVoList);
                // 添加需要的消息数量
                start += batchSize;
            } while (true);
            messageService.removeMsgByDate(userId, targetDate);
        }

    }

    private void saveOfflineMessage(ChatMsgDTO message) {
        // 存储到redis离线消息集合
        messageService.saveOfflineMsg(message.getToId(), message);
    }



}