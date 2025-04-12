package com.li.chat.netty.mq.producer;

import com.li.chat.common.enums.MessageMqEnum;
import com.li.chat.domain.DTO.message.MessageDTO;
import com.li.chat.netty.autoconfigure.socketio.SocketIoProperties;
import com.li.chat.netty.vo.MsgMqVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author malaka
 */
@Slf4j
@Component
public class MessageProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private SocketIoProperties socketIoProperties;

    public void forwardMessage(String toNodeId, MessageDTO message) {

        MsgMqVo msgMqVo = MsgMqVo.builder().nodeId(toNodeId).message(message).build();

        log.info("转发消息: {}", message);
        rocketMQTemplate.asyncSend(MessageMqEnum.TOPIC_FORWARD_MESSAGE, msgMqVo, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("转发消息成功 id: {}", sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("转发消息失败, error: {}", throwable.getMessage(), throwable);
            }
        });
    }


    public void broadcastGroupMessage(MessageDTO message) {

        log.info("广播群消息: {}", message);
        MsgMqVo msgMqVo = MsgMqVo.builder().nodeId(socketIoProperties.getNodeId()).message(message).build();

        rocketMQTemplate.asyncSend(MessageMqEnum.TOPIC_FORWARD_MESSAGE, msgMqVo, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("广播群消息成功 id: {}", sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("广播群消息失败, error: {}", throwable.getMessage(), throwable);
            }
        });
    }


    public void saveHistoryMessage(MessageDTO message) {

        log.info("存储历史消息: {}", message);
        rocketMQTemplate.asyncSend(MessageMqEnum.TOPIC_SAVE_HISTORY_MESSAGE, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("存储历史消息成功 id: {}", sendResult.getMsgId());
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("存储历史消息失败, error: {}", throwable.getMessage(), throwable);
            }
        });
    }
}
