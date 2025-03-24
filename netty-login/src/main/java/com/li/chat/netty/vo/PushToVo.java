package com.li.chat.netty.vo;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 消息接收人
 */
@Data
@Builder
@Accessors(chain = true) // 链式调用
public class PushToVo {

    /**
     * 接收人id
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

}
