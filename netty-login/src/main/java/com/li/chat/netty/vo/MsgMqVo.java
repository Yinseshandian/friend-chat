package com.li.chat.netty.vo;

import com.li.chat.domain.DTO.message.ChatMsgDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author malaka
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MsgMqVo {

    private String nodeId;

    private ChatMsgDTO message;

}
