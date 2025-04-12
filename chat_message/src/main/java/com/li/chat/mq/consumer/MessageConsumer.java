package com.li.chat.mq.consumer;

import cn.hutool.core.bean.BeanUtil;
import com.li.chat.common.enums.MessageMqEnum;
import com.li.chat.domain.DTO.message.MessageDTO;
import com.li.chat.entity.HistoryMessage;
import com.li.chat.service.HistoryMessageService;
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
    private HistoryMessageService historyMessageService;

    @Component
    @RocketMQMessageListener(
            topic = MessageMqEnum.TOPIC_SAVE_HISTORY_MESSAGE,
            consumerGroup = "save-history-message-group"
    )
    public class ForwardMessageConsumer implements RocketMQListener<MessageDTO> {
        @Override
        public void onMessage(MessageDTO messageDTO) {
            log.info("保存历史消息：{}", messageDTO);
            HistoryMessage historyMessage = HistoryMessage.builder().build();
            BeanUtil.copyProperties(messageDTO, historyMessage);
            historyMessageService.create(historyMessage);
        }
    }

}
