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

    public static final String CLIENT_UID_KEY = "userId";

    protected PushBodyVo buildPushBody(ChatMsgDTO message){
        if ("GROUP".equals(message.getTalkType())) {
            return buildGroupPushBody(message);
        }else if ("SINGLE".equals(message.getTalkType())) {
            return buildSinglePushBody(message);
        }
        return null;
    }


    protected PushBodyVo buildSinglePushBody(ChatMsgDTO message) {
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
        pushBodyVo.setCreateTime(DateUtil.format(message.getCreateTime(), DatePattern.NORM_DATETIME_FORMAT));
        // 发送人
        pushBodyVo.setFromInfo(BeanUtil.toBean(from, PushFromVo.class).setUserType(from.getUserType()));

        return pushBodyVo;
    }

    protected PushBodyVo buildGroupPushBody(ChatMsgDTO message) {
        Long groupId = message.getToId();
        Long userId = message.getFromId();
        Long msgId = message.getId();
        String key = buildRoomKey(groupId);

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
        pushBodyVo.setCreateTime(DateUtil.format(message.getCreateTime(), DatePattern.NORM_DATETIME_FORMAT));
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

    protected Long generateMsgId() {
        // 雪花id
        return IdUtil.getSnowflake().nextId();
    }

    protected abstract String buildRoomKey(Object k);
}
