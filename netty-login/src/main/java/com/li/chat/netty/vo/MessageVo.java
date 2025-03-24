package com.li.chat.netty.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author malaka
 */
@Data
public class MessageVo {

    private Long msgId;
    private Long fromId; // 发送者Id
    private Long toId; // 接收者Id
    private Date time; // 消息发送时间
    private String content; // 消息内容
    private Integer msgType; // 消息的类型：emoji/text/img/file/sys
    private String talkType; // 聊天类型

}
