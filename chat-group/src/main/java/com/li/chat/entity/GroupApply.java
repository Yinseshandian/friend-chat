package com.li.chat.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author malaka
 */

@Getter
@Setter
@Entity
@Table(name = "chat_group_apply")
public class GroupApply extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long groupId;

    private Long inviteUserId; // 群成员邀请id

    private Long userId;

    private Long processedBy; // 处理人Id

    private String message;

    private Integer type; // 类型 0用户申请 1群成员邀请

    private Integer status; // 申请状态 0未处理 1同意 2拒绝

}
