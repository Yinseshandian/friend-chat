package com.li.chat.netty.vo;

import com.li.chat.common.enums.PushMsgTypeEnum;
import lombok.Data;

/**
 * @author malaka
 */
@Data
public class SendGroupVo {

    private Long groupId;

    private Long fromId;

    private PushMsgTypeEnum msgType;

    private String content;
}
