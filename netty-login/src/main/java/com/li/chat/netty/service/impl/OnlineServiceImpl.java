package com.li.chat.netty.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.corundumstudio.socketio.SocketIOClient;
import com.li.chat.common.enums.ChatOnlineEnum;
import com.li.chat.common.enums.MessageClientEnum;
import com.li.chat.common.enums.RedisCachePrefixEnum;
import com.li.chat.common.enums.TalkTypeEnum;
import com.li.chat.common.utils.RedisCache;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.domain.DTO.message.ChatMsgDTO;
import com.li.chat.feign.GroupManagementFeign;
import com.li.chat.feign.GroupMemberFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.netty.autoconfigure.socketio.SocketIoProperties;
import com.li.chat.netty.manager.UserClientManager;
import com.li.chat.netty.service.OnlineService;
import com.li.chat.netty.vo.SendGroupVo;
import com.li.chat.netty.vo.SendVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author malaka
 */
@Service
public class OnlineServiceImpl implements OnlineService {

    @Autowired
    protected UserFeign userFeign;

    @Autowired
    protected RedisCache redisCache;

    @Autowired
    protected SocketIoProperties socketIoProperties;

    @Autowired
    protected UserClientManager userClientManager;


    @Autowired
    protected GroupManagementFeign groupManagementFeign;


    @Override
    public Long goOnline(String token, SocketIOClient client) {
        Long userId = validateToken(token);
        if (userId == null) {
            return null;
        }
        String redisKey = buildOnlineKey(userId);

        redisCache.setCacheObject(redisKey, socketIoProperties.getNodeId());

        client.set(MessageClientEnum.CLIENT_UID_KEY, userId);
        userClientManager.addClient(userId, client);

        return userId;
    }

    @Override
    public void goGroupOnline(SocketIOClient client) {
        Long userId = client.get(MessageClientEnum.CLIENT_UID_KEY);
        if (userId == null) {
            return;
        }
        // 加入用户所在的群 room
        List<GroupDTO> groupDTOList = groupManagementFeign.findAllGroupByUserId(userId);
        for (GroupDTO groupDTO : groupDTOList) {
            Long id = groupDTO.getId();
            String key = buildRoomKey(id);
            client.joinRoom(key);
        }
    }

    protected String buildRoomKey(Object s) {
        return MessageClientEnum.ROOM_GROUP_PREFIX + s;
    }

    @Override
    public boolean goOffline(SocketIOClient client) {
        Long userId = client.get(MessageClientEnum.CLIENT_UID_KEY);
        String redisKey = buildOnlineKey(userId);
        redisCache.deleteObject(redisKey);
        client.disconnect();
        return true;
    }

    @Override
    public ChatOnlineEnum checkOnline(Long userId) {
        String nodeId = redisCache.getCacheObject(buildOnlineKey(userId));
        if (nodeId == null) {
            return ChatOnlineEnum.OFFLINE;
        }
        if (ObjectUtil.equal(nodeId, socketIoProperties.getNodeId())) {
            return ChatOnlineEnum.ONLINE_CURRENT;
        }
        return ChatOnlineEnum.ONLINE_OTHER;
    }


    private Long validateToken(String token) {
        return userFeign.checkLoginOnToken(token);
    }

    private String buildOnlineKey(Long userId) {
        return  RedisCachePrefixEnum.NETTY_CHAT_ONLINE + userId;
    }

}
