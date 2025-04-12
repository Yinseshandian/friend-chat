package com.li.chat.netty.listener;

import cn.hutool.core.util.IdUtil;
import com.corundumstudio.socketio.SocketIOServer;
import com.li.chat.common.utils.RedisCache;
import com.li.chat.feign.FriendFeign;
import com.li.chat.feign.GroupManagementFeign;
import com.li.chat.feign.GroupMemberFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.netty.manager.UserClientManager;
import com.li.chat.netty.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author malaka
 */
public abstract class AbstractEventListener {

    @Autowired
    protected MessageService messageService;

    @Autowired
    protected SocketIOServer socketIOServer;

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
    protected FriendFeign friendFeign;

    protected Long generateMsgId() {
        // 雪花id
        return IdUtil.getSnowflake().nextId();
    }

}
