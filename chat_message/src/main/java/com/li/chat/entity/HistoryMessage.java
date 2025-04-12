package com.li.chat.entity;

import lombok.*;

import javax.persistence.*;

/**
 * @author malaka
 * 历史消息
 */
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "chat_history_message")
public class HistoryMessage extends BaseEntity{

    @Id
    private Long id;

    private Long fromId;

    private Long toId;

    private String msgType; // 消息类型

    private String talkType; // 聊天类型

    @Lob
    private String content;

}
