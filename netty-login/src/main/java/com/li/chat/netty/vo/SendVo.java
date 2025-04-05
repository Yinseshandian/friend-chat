package com.li.chat.netty.vo;

import com.li.chat.common.enums.PushMsgTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author malaka
 */
@Data
public class SendVo {

    private Long userId;

    private Long fromId;

    private PushMsgTypeEnum msgType;

    private String content;

}
