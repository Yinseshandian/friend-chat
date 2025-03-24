package com.li.chat.netty.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息发送人
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushParamVo {

    /**
     * 消息id
     */
    private Long msgId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户头像
     */
    private String portrait;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 临时参数
     */
    private Long toId;

}
