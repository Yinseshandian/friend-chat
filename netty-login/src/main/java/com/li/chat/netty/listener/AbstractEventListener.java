package com.li.chat.netty.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.corundumstudio.socketio.SocketIOServer;
import com.li.chat.common.enums.PushBodyTypeEnum;
import com.li.chat.common.utils.RedisCache;
import com.li.chat.domain.DTO.FriendDTO;
import com.li.chat.domain.DTO.GroupDTO;
import com.li.chat.domain.DTO.GroupMemberDTO;
import com.li.chat.domain.DTO.UserDTO;
import com.li.chat.domain.DTO.message.ChatMsgDTO;
import com.li.chat.feign.FriendFeign;
import com.li.chat.feign.GroupManagementFeign;
import com.li.chat.feign.GroupMemberFeign;
import com.li.chat.feign.UserFeign;
import com.li.chat.netty.manager.UserClientManager;
import com.li.chat.netty.service.MessageService;
import com.li.chat.netty.vo.PushBodyVo;
import com.li.chat.netty.vo.PushFromVo;
import com.li.chat.netty.vo.PushMsgVo;
import com.li.chat.netty.vo.PushToVo;
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
