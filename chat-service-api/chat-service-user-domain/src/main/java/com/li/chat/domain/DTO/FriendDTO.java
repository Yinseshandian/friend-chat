package com.li.chat.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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


}
