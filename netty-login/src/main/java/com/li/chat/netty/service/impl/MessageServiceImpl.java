package com.li.chat.netty.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.li.chat.common.enums.*;
import com.li.chat.common.utils.RedisCache;
import com.li.chat.domain.DTO.FriendDTO;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.domain.DTO.GroupMemberDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.domain.DTO.message.MessageDTO;
import com.li.chat.feign.FriendFeign;
import com.li.chat.feign.GroupManagementFeign;
import com.li.chat.feign.GroupMemberFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.netty.autoconfigure.message.OfflineMsgProperties;
import com.li.chat.netty.listener.GroupEventListener;
import com.li.chat.netty.manager.UserClientManager;
import com.li.chat.netty.mq.producer.MessageProducer;
import com.li.chat.netty.service.MessageService;
import com.li.chat.netty.service.OnlineService;
import com.li.chat.netty.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author malaka
 */
@Component
public class MessageServiceImpl implements MessageService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OfflineMsgProperties offlineMsgProperties;

    @Autowired
    protected RedisCache redisCache;

    @Autowired
    protected UserFeign userFeign;

    @Autowired
    protected UserClientManager userClientManager;

    @Autowired
    protected GroupMemberFeign groupMemberFeign;

    @Autowired
    protected GroupManagementFeign groupManagementFeign;

    @Autowired
    protected OnlineService onlineService;

    @Autowired
    protected SocketIOServer socketIOServer;

    @Autowired
    protected FriendFeign friendFeign;

    @Autowired
    private MessageProducer messageProducer;


    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private final static String FORMAT_DATETIME = "yyyy-MM-dd HH:mm";

    @Override
    public PushBodyVo buildPushBody(MessageDTO message) {
        if ("GROUP".equals(message.getTalkType())) {
            return buildGroupPushBody(message);
        }else if ("SINGLE".equals(message.getTalkType())) {
            return buildSinglePushBody(message);
        }
        return null;
    }

    protected PushBodyVo buildSinglePushBody(MessageDTO message) {
        PushMsgVo pushMsgVo = new PushMsgVo()
                .setMsgType(message.getMsgType())
                .setTop("N")
                .setDisturb("N")
                .setContent(message.getContent());

        PushFromVo from = new PushFromVo();
        FriendDTO friendDTO = friendFeign.info(message.getToId(), message.getFromId());

        from.setUserId(message.getFromId());
        from.setUserType("normal");
        from.setNickName(friendDTO.getRemark());
        from.setPortrait(friendDTO.getAvatar());

        PushBodyVo pushBodyVo = new PushBodyVo(message.getId(), PushBodyTypeEnum.MSG, pushMsgVo);
        pushBodyVo.setCreateTime(DateUtil.format(message.getCreateTime(),FORMAT_DATETIME));
        // 发送人
        pushBodyVo.setFromInfo(BeanUtil.toBean(from, PushFromVo.class).setUserType(from.getUserType()));
        return pushBodyVo;
    }

    private PushBodyVo buildGroupPushBody(MessageDTO message) {
        Long groupId = message.getToId();
        Long userId = message.getFromId();
        Long msgId = message.getId();
        String key = GroupEventListener.buildRoomKey(groupId);

        PushMsgVo pushMsgVo = new PushMsgVo()
                .setMsgType(message.getMsgType())
                .setTop("N")
                .setDisturb("N")
                .setContent(message.getContent());

        GroupDTO group = groupManagementFeign.findGroupById(groupId);

        PushFromVo from = new PushFromVo();
        GroupMemberDTO memberDTO = groupMemberFeign.findByGroupIdAndUserId(groupId, userId);
        UserDTO user = userFeign.findUserById(userId);

        from.setUserId(userId);
        from.setUserType("normal");
        from.setNickName(memberDTO.getNickname());
        from.setPortrait(user.getAvatar());

        // 推送体
        PushBodyVo pushBodyVo = new PushBodyVo(msgId, PushBodyTypeEnum.MSG, pushMsgVo);
        pushBodyVo.setCreateTime(DateUtil.format(message.getCreateTime(),FORMAT_DATETIME));
        // 发送人
        pushBodyVo.setFromInfo(BeanUtil.toBean(from, PushFromVo.class).setUserType(from.getUserType()));
        // 群
        PushToVo to = PushToVo.builder()
                .userId(groupId)
                .nickName(group.getName())
                .portrait(group.getPhoto())
                .build();
        pushBodyVo.setGroupInfo(to);
        return pushBodyVo;
    }

    @Override
    public int saveOfflineMsg(Long toId, MessageDTO message) {
        // 存储到redis离线消息集合
        String key = RedisCachePrefixEnum.NETTY_CHAT_OFFLINE_MSG + message.getToId() + ":" + sdf.format(new Date());
        Boolean isNew = redisTemplate.hasKey(key);
        ListOperations listOperations = redisTemplate.opsForList();

        listOperations.rightPush(key, JSONUtil.parse(message).toString());
        if (isNew) {
            redisTemplate.expire(key, offlineMsgProperties.getExpired(), offlineMsgProperties.getTimeUnit());
        }
        return 1;
    }

    @Override
    public List<MessageDTO> getOfflineMsgByUid(Long userId, Date date, long start, long end) {
        String key = RedisCachePrefixEnum.NETTY_CHAT_OFFLINE_MSG + userId + ":" + sdf.format(date);
        ListOperations listOperations = redisTemplate.opsForList();
        List<String> msgJsonList = listOperations.range(key, start, end);
        List<MessageDTO> msgList = JSONUtil.toList(msgJsonList.toString(), MessageDTO.class);
        return msgList;
    }

    @Override
    public void removeMsgByDate(Long userId, Date date) {
        String key = RedisCachePrefixEnum.NETTY_CHAT_OFFLINE_MSG + userId + ":" + sdf.format(date);
        redisTemplate.delete(key);
    }

    @Override
    public MessageDTO buildSingleMessageDTO(SendVo sendVo) {
        long msgId = IdUtil.getSnowflake().nextId();
        MessageDTO message = new MessageDTO()
                .setId(msgId)
                .setFromId(sendVo.getFromId())
                .setToId(sendVo.getUserId())
                .setMsgType(sendVo.getMsgType().getCode())
                .setTalkType(TalkTypeEnum.SINGLE.getCode())
                .setContent(sendVo.getContent())
                .setCreateTime(DateUtil.toLocalDateTime(DateUtil.date()));
        return message;
    }

    @Override
    public MessageDTO buildGroupMessageDTO(SendGroupVo sendVo) {
        long msgId = IdUtil.getSnowflake().nextId();
        MessageDTO message = new MessageDTO()
                .setFromId(sendVo.getFromId())
                .setToId(sendVo.getGroupId())
                .setMsgType(sendVo.getMsgType().getCode())
                .setTalkType(TalkTypeEnum.GROUP.getCode())
                .setContent(sendVo.getContent())
                .setId(msgId)
                .setCreateTime(DateUtil.toLocalDateTime(DateUtil.date()));
        return message;
    }

    @Override
    public MessageStatusEnum sendGroupMessage(MessageDTO message) {
        Long userId = message.getFromId();
        if (!groupMemberFeign.isGroupMember(userId, message.getToId())) {
            return MessageStatusEnum.GROUP_INFO_NOT_EXIST;
        }

        // 先广播消息
        messageProducer.broadcastGroupMessage(message);
        // 发送在线消息
        Set<Long> sendUserIds = sendGroupOnlineMessage(message);

        // 离线消息
        List<GroupMemberDTO> memberDTOList = groupMemberFeign.findAllByGroupId(message.getId());
        // 移除已发送的用户
        for (int i = 0; i < memberDTOList.size(); i++) {
            GroupMemberDTO groupMemberDTO = memberDTOList.get(i);
            if (sendUserIds.contains(groupMemberDTO.getUserId())) {
                memberDTOList.remove(i);
                i--;
            }
        }
        // 保存离线消息
        List<Long> uidList = memberDTOList.stream().map(GroupMemberDTO::getUserId).collect(Collectors.toList());
        for (Long id : uidList) {
            saveOfflineMsg(id, message);
        }

        messageProducer.saveHistoryMessage(message);
        return MessageStatusEnum.OK;
    }

    @Override
    public Set<Long> sendGroupOnlineMessage(MessageDTO message) {
        PushBodyVo pushBodyVo = buildPushBody(message);

        Set<Long> sendUserIds = new HashSet<>();
        Long groupId = message.getToId();
        String key = GroupEventListener.buildRoomKey(groupId);
        Collection<SocketIOClient> clients = socketIOServer.getRoomOperations(key).getClients();
        for (SocketIOClient c : clients) {
            Long id = c.get(MessageClientEnum.CLIENT_UID_KEY);
            sendUserIds.add(id);
            if (message.getFromId().equals(id)) {
                continue;
            }
            c.sendEvent(MessageSocketioEvent.SEND_NEW_MESSAGE, pushBodyVo);
        }
        return sendUserIds;
    }

    @Override
    public MessageStatusEnum sendSingleMessage(MessageDTO message) {

        Long userId = message.getFromId();
        Long friendId = message.getToId();

        if (!friendFeign.isFriend(userId, friendId)) {
            return MessageStatusEnum.FRIEND_TO;
        }
        // 检查接收方是否在线
        String onlineNodeId = this.onlineService.getOnlineNodeId(friendId);
        ChatOnlineEnum chatOnlineEnum = this.onlineService.checkOnline(friendId);

        PushBodyVo pushBodyVo = buildPushBody(message);

        Set<SocketIOClient> fClients = userClientManager.getClients(message.getToId());
        if (chatOnlineEnum == ChatOnlineEnum.ONLINE_CURRENT) {
            //  在线：直接推送
            for (SocketIOClient c : fClients) {
                c.sendEvent(MessageSocketioEvent.SEND_NEW_MESSAGE, pushBodyVo);
            }
        }
        if (chatOnlineEnum == ChatOnlineEnum.ONLINE_OTHER){
            // TODO 其他服务器在线：转发
            messageProducer.forwardMessage(onlineNodeId, message);
        }
        if (chatOnlineEnum == ChatOnlineEnum.OFFLINE){
            //  离线：存储到离线表
            saveOfflineMsg(message.getToId(), message);
        }
        messageProducer.saveHistoryMessage(message);
        return MessageStatusEnum.OK;
    }

}
