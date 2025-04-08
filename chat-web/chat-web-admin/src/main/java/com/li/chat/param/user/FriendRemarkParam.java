package com.li.chat.param.user;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author malaka
 */
@Data
public class FriendRemarkParam {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "好友ID不能为空")
    private Long friendId;

    private String userRemark;

    private String friendRemark;
}