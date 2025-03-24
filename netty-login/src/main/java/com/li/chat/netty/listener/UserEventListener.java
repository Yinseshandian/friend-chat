package com.li.chat.netty.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.corundumstudio.socketio.AckCallback;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.li.chat.common.enums.PushBodyTypeEnum;
import com.li.chat.common.enums.RedisCachePrefixEnum;
import com.li.chat.common.utils.RedisCache;
import com.li.chat.domain.DTO.FriendDTO;
import com.li.chat.domain.DTO.message.ChatMsgDTO;
import com.li.chat.feign.FriendFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.netty.manager.UserClientManager;
import com.li.chat.netty.service.MessageService;
import com.li.chat.netty.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Order(1)
public class UserEventListener extends AbstractEventListener{

    // 自定义事件名称常量
    public static final String EVENT_ONLINE = "online";
    private static final String EVENT_SEND_MESSAGE = "send_message";
    private static final String EVENT_MESSAGE_ACK = "message_ack";
    private static final String EVENT_PULL_OFFLINE_MESSAGE = "pull_offline_message";
    private static final String SEND_NEW_MESSAGE = "new_message";
    private static final String SEND_OFFLINE_MESSAGE = "offline_message";
    private static final String EVENT_LOGOUT = "logout";

    @OnConnect
    public void eventOnConnect(SocketIOClient client) {
        Map<String, List<String>> urlParams = client.getHandshakeData().getUrlParams();
        log.info("客户端唯一标识为：{}", client.getSessionId());
        log.info("链接开启，urlParams：{}", urlParams);
    }

    @OnEvent("e_login")
    public void test(SocketIOClient client, AckRequest ackRequest, Map<String, String> data) {
        client.sendEvent("test1", new AckCallback<Object>(Object.class) {
            @Override
            public void onSuccess(Object o) {
                System.out.println(o);
            }
        }, "message");

        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData("success");
        }
    }

    @OnEvent(EVENT_ONLINE)
    public void onLogin(SocketIOClient client, AckRequest ackRequest, Map<String, String> data) {
        String token = data.get("token");
        HashMap<String, String> map = new HashMap<>();
        Long userId = validateToken(token);
        if (userId == null) {
            map.put("code", "1");
            map.put("msg", "未登录");
            sendAck(ackRequest, map);
            return;
        }

        // 2. 绑定用户ID与Socket连接
        client.set(CLIENT_UID_KEY, userId);

        // 3. 记录在线状态到Redis（存储节点信息，当前节点为node1）
        String redisKey = RedisCachePrefixEnum.NETTY_CHAT_ONLINE + userId;
        redisCache.setCacheObject(redisKey, "node1", 30, TimeUnit.MINUTES);

        userClientManager.addClient(userId, client);
        // 返回登录成功ACK

        map.put("code", "0");
        map.put("msg", "上线成功");
        sendAck(ackRequest, map);
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        Long userId = client.get(CLIENT_UID_KEY);
        if (userId != null) {
            redisCache.deleteObject(RedisCachePrefixEnum.NETTY_CHAT_ONLINE + userId);
            System.out.println("用户下线: " + userId);
        }
        userClientManager.removeClient(userId, client);
        client.disconnect();
    }

    @OnEvent(EVENT_SEND_MESSAGE)
    public void onSendMessage(SocketIOClient client, AckRequest ackRequest, SendVo sendVo) {
        SendStatusVo.SendStatusVoBuilder statusVoBuilder = SendStatusVo.builder();
        try{
            Long userId = client.get(CLIENT_UID_KEY);
            if (userId == null) {
                sendAck(ackRequest, statusVoBuilder.status("2"));
                return;
            }
            Long friendId = sendVo.getUserId();

            if (!friendFeign.isFriend(userId, friendId)) {
                sendAck(ackRequest, statusVoBuilder.status("1").build());
            }
            // 2. 检查接收方是否在线
            String receiverKey = RedisCachePrefixEnum.NETTY_CHAT_ONLINE + sendVo.getUserId();
            boolean isOnline = redisCache.hasKey(receiverKey);

            Long msgId = generateMsgId();
            ChatMsgDTO message = new ChatMsgDTO()
                    .setId(msgId)
                    .setFromId(userId)
                    .setToId(friendId)
                    .setMsgType(sendVo.getMsgType().getCode())
                    .setTalkType("SINGLE")
                    .setContent(sendVo.getContent())
                    .setCreateTime(DateUtil.date());

            PushBodyVo pushBodyVo = buildPushBody(message);

            Set<SocketIOClient> fClients = userClientManager.getClients(message.getToId());
            if (isOnline) {
                // 3. 在线：直接推送
                for (SocketIOClient c : fClients) {
                    c.sendEvent(SEND_NEW_MESSAGE, pushBodyVo);
                }

            } else {
                // 4. 离线：存储到离线表
                saveOfflineMessage(message);
            }

            // 返回ACK给发送方
            sendAck(ackRequest, statusVoBuilder.msgId(msgId).status("0").build());
        }catch (Exception e) {
            sendAck(ackRequest, statusVoBuilder.status("1").build());
        }

    }

    private void sendAck(AckRequest ackRequest, Object... data) {
        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData(data);
        }
    }

    @OnEvent(EVENT_MESSAGE_ACK)
    public void onMessageAck(SocketIOClient client, Map<String, String> data) {
        String msgId = data.get("msgId");
        String status = data.get("status"); // delivered/read

    }

    @OnEvent(EVENT_PULL_OFFLINE_MESSAGE)
    private void pullOfflineMessages(SocketIOClient client) {
        Long userId = client.get(CLIENT_UID_KEY);
        if (userId == null) {
            return;
        }
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
                    pushBodyVoList.add(buildPushBody(chatMsgDTO));
                }
                client.sendEvent(SEND_OFFLINE_MESSAGE, pushBodyVoList);
                // 添加需要的消息数量
                start += batchSize;
            } while (true);
            messageService.removeMsg(userId, targetDate);
        }

    }

    private Long validateToken(String token) {
        // 实际应调用Auth服务验证Token并返回UserID
        return userFeign.checkLoginOnToken(token);
    }


    private void sendOnlineMessage(ChatMsgDTO message) {
        Set<SocketIOClient> clients = userClientManager.getClients(message.getToId());
        for (SocketIOClient client : clients) {
            client.sendEvent(SEND_NEW_MESSAGE, new AckCallback<Object>(Object.class) {
                @Override
                public void onSuccess(Object o) {
                    System.out.println(o);
                }
            }, message);
        }
    }

    private void saveOfflineMessage(ChatMsgDTO message) {
        // 存储到redis离线消息集合
        messageService.saveOfflineMsg(message.getToId(), message);
    }

    @Override
    protected Long generateMsgId() {
        // 雪花id
        return IdUtil.getSnowflake().nextId();
    }

    @Override
    protected String buildRoomKey(Object k) {
        return "" + k;
    }

}