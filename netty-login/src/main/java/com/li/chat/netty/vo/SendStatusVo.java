package com.li.chat.netty.vo;

import com.li.chat.common.enums.MessageStatusEnum;
import lombok.Builder;
import lombok.Data;

/**
 * @author malaka
 */
@Data
@Builder
public class SendStatusVo {

    /**
     * 发送状态
     */
    private MessageStatusEnum status;

    /**
     * 消息id
     */
    private Long msgId;


}
