package com.li.chat.netty.vo;

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
    private String status;

    /**
     * 消息id
     */
    private Long msgId;


}
