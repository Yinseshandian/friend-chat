package com.li.chat.netty.vo;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息发送人
 */
@Data
@Accessors(chain = true) // 链式调用
public class PushFromVo {

    /**
     * 发送人
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
     * 用户类型（normal、self、turing、weather、translation）
     */
    private String userType;

}
