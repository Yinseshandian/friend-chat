package com.li.chat.netty.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.li.chat.common.enums.PushBodyTypeEnum;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.domain.DTO.GroupMemberDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.domain.DTO.message.ChatMsgDTO;
import com.li.chat.feign.GroupManagementFeign;
import com.li.chat.feign.GroupMemberFeign;
import com.li.chat.netty.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Order(0)
public class GroupEventListener extends AbstractEventListener{

    // 自定义事件名称常量
    private static final String EVENT_GROUP_ONLINE = "group_online";
    private static final String EVENT_SEND_GROUP_MESSAGE = "send_group_message";
    private static final String SEND_NEW_MESSAGE = "new_message";
    public static final String ROOM_GROUP_KEY = "room_group_";

    @OnConnect
    public void eventOnConnect(SocketIOClient client) {
        Map<String, List<String>> urlParams = client.getHandshakeData().getUrlParams();
    }


    @OnEvent(EVENT_GROUP_ONLINE)
    public void onLogin(SocketIOClient client, AckRequest ackRequest, Map<String, String> data) {
        Long userId = client.get(CLIENT_UID_KEY);
        if (userId == null) {
            return;
        }
        List<GroupDTO> groupDTOList = groupManagementFeign.findAllGroupByUserId(userId);
        for (GroupDTO groupDTO : groupDTOList) {
            Long id = groupDTO.getId();
            String key = buildRoomKey(id);
            client.joinRoom(key);
        }
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {

    }

    @OnEvent(EVENT_SEND_GROUP_MESSAGE)
    public void onSendMessage(SocketIOClient client, AckRequest ackRequest, SendGroupVo sendVo) {
        SendStatusVo.SendStatusVoBuilder statusVoBuilder = SendStatusVo.builder();
        try{
            Long userId = client.get(CLIENT_UID_KEY);
            if (userId == null) {
                sendAck(ackRequest, statusVoBuilder.status("2"));
                return;
            }
            Long groupId = sendVo.getGroupId();

            if (!groupMemberFeign.isGroupMember(userId, groupId)) {
                sendAck(ackRequest, statusVoBuilder.status("6").build());
                return;
            }
            // 保存数据
            Long msgId = generateMsgId();
            ChatMsgDTO message = new ChatMsgDTO()
                    .setFromId(userId)
                    .setToId(groupId)
                    .setMsgType(sendVo.getMsgType().getCode())
                    .setTalkType("GROUP")
                    .setContent(sendVo.getContent())
                    .setId(msgId)
                    .setCreateTime(DateUtil.date());

            // 发送在线消息
            Set<Long> sendUserIds = sendOnlineMessage(message);

            // 返回ACK给发送方
            sendAck(ackRequest, statusVoBuilder.msgId(msgId).status("0").build());

            // 离线消息
            List<GroupMemberDTO> memberDTOList = groupMemberFeign.findAllByGroupId(groupId);
            // 移除已发送的用户
            for (int i = 0; i < memberDTOList.size(); i++) {
                GroupMemberDTO groupMemberDTO = memberDTOList.get(i);
                if (sendUserIds.contains(groupMemberDTO.getUserId())) {
                    memberDTOList.remove(i);
                    i--;
                }
            }
            List<Long> uidList = memberDTOList.stream().map(GroupMemberDTO::getUserId).collect(Collectors.toList());
            saveOfflineMessage(uidList, message);

        }catch (Exception e) {
            sendAck(ackRequest, statusVoBuilder.status("1").build());
        }

    }

    private void sendAck(AckRequest ackRequest, Object... data) {
        if (ackRequest.isAckRequested()) {
            ackRequest.sendAckData(data);
        }
    }

    private Set<Long> sendOnlineMessage(ChatMsgDTO message) {
        PushBodyVo pushBodyVo = buildPushBody(message);

        Set<Long> sendUserIds = new HashSet<>();
        Long groupId = message.getToId();
        String key = buildRoomKey(groupId);
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(key).getClients();
        for (SocketIOClient c : clients) {
            Long id = c.get(CLIENT_UID_KEY);
            sendUserIds.add(id);
            if (message.getFromId().equals(id)) {
                continue;
            }
            c.sendEvent(SEND_NEW_MESSAGE, pushBodyVo);
        }
        return sendUserIds;
    }

    private void saveOfflineMessage(List<Long> userIdList, ChatMsgDTO message) {
        for (Long id : userIdList) {
            messageService.saveOfflineMsg(id, message);
        }
    }

    @Override
    protected String buildRoomKey(Object s) {
        return ROOM_GROUP_KEY + s;
    }

}