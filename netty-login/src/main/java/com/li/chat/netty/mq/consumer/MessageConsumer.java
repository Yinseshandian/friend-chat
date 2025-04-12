package com.li.chat.netty.mq.consumer;

import cn.hutool.core.util.ObjectUtil;
import com.li.chat.common.enums.MessageMqEnum;
import com.li.chat.domain.DTO.message.MessageDTO;
import com.li.chat.netty.autoconfigure.socketio.SocketIoProperties;
import com.li.chat.netty.service.MessageService;
import com.li.chat.netty.vo.MsgMqVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author malaka
 */
@Slf4j
@Component
public class MessageConsumer {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SocketIoProperties socketIoProperties;

    @Component
    @RocketMQMessageListener(
            topic = MessageMqEnum.TOPIC_FORWARD_MESSAGE,
            consumerGroup = "forward-message-group",
            messageModel = MessageModel.CLUSTERING
    )
    public class ForwardMessageConsumer implements RocketMQListener<MsgMqVo> {
        @Override
        public void onMessage(MsgMqVo msgMqVo) {
            String nodeId = msgMqVo.getNodeId();
            if (ObjectUtil.equal(nodeId, socketIoProperties.getNodeId())) {
                MessageDTO message = msgMqVo.getMessage();
                log.info("node: {} 转发消息: {}",nodeId,  message);
                messageService.sendSingleMessage(message);
            }
        }
    }

    @Component
    @RocketMQMessageListener(
            topic = MessageMqEnum.TOPIC_BROADCAST_GROUP_MESSAGE,
            consumerGroup = "broadcast-group-message-group",
            messageModel = MessageModel.CLUSTERING
    )
    public class BroadcastGroupMessageConsumer implements RocketMQListener<MsgMqVo> {
        @Override
        public void onMessage(MsgMqVo msgMqVo) {
            String nodeId = msgMqVo.getNodeId();
            if (!ObjectUtil.equal(nodeId, socketIoProperties.getNodeId())) {
                MessageDTO message = msgMqVo.getMessage();
                log.info("node: {} 广播群消息: {}",nodeId , message);
                messageService.sendGroupOnlineMessage(message);
            }

        }
    }

}
