package com.li.chat.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author malaka
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FriendDTO {

    private Long id;
    // 用户id
    private Long userId;
    // 好友id
    private Long friendId;
    // 备注
    private String remark;

    private String nickname;

    private String username;

    private String avatar;

    private LocalDateTime createTime;
    // 好友状态
    private Integer status;

    // 后台用
    private String userRemark;

    private String userAvatar;

    private String userUsername;

    private String friendRemark;

    private String friendAvatar;

    private String friendUsername;


}
