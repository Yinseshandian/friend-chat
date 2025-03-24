package com.li.chat.netty.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.li.chat.common.enums.RedisCachePrefixEnum;
import com.li.chat.domain.DTO.message.ChatMsgDTO;
import com.li.chat.netty.config.OfflineMsgConfig;
import com.li.chat.netty.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author malaka
 */
@Component
public class MessageServiceImpl implements MessageService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OfflineMsgConfig offlineMsgConfig;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public int saveOfflineMsg(Long toId, ChatMsgDTO message) {
        // 存储到redis离线消息集合
        String key = RedisCachePrefixEnum.NETTY_CHAT_OFFLINE_MSG + toId + ":" + sdf.format(new Date());
        Boolean isNew = redisTemplate.hasKey(key);
        ListOperations listOperations = redisTemplate.opsForList();

        listOperations.rightPush(key, JSONUtil.parse(message).toString());
        if (isNew) {
            redisTemplate.expire(key, offlineMsgConfig.getExpired(), offlineMsgConfig.getTimeUnit());
        }
        return 1;
    }

    @Override
    public List<ChatMsgDTO> getOfflineMsgByUid(Long userId, Date date, long start, long end) {
        String key = RedisCachePrefixEnum.NETTY_CHAT_OFFLINE_MSG + userId + ":" + sdf.format(date);
        ListOperations listOperations = redisTemplate.opsForList();
        List<String> msgJsonList = listOperations.range(key, start, end);
        List<ChatMsgDTO> msgList = JSONUtil.toList(msgJsonList.toString(), ChatMsgDTO.class);
        return msgList;
    }

    @Override
    public void removeMsg(Long userId, Date date) {
        String key = RedisCachePrefixEnum.NETTY_CHAT_OFFLINE_MSG + userId + ":" + sdf.format(date);
        redisTemplate.delete(key);
    }
}
