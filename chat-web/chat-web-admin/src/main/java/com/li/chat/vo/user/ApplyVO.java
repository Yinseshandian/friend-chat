package com.li.chat.vo.user;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author malaka
 */
@Data
public class ApplyVO {
    private Long id;
    private Long fromId;
    private Long toId;
    private String message;
    private String remark;
    private Integer status; // 申请状态 0未处理 1同意 2拒绝
    private LocalDateTime createTime;

    // 申请人信息
    private String fromUsername;
    private String fromNickname;
    private String fromAvatar;

    // 被申请人信息
    private String toUsername;
    private String toNickname;
    private String toAvatar;
}