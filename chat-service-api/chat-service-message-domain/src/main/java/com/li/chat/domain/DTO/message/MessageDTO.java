package com.li.chat.domain.DTO.message;

import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 聊天消息实体类
 * q3z3
 * </p>
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true) // 链式调用
@ToString
public class MessageDTO {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 发送人
     */
    private Long fromId;
    /**
     * 接收人
     */
    private Long toId;
    /**
     * 消息类型
     */
    private String msgType;
    /**
     * 消息类型
     */
    private String talkType;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 查询用
      */
    private Long startTime;

    private Long endTime;
}
