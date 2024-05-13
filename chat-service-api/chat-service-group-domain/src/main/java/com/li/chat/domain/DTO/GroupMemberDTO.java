package com.li.chat.domain.DTO;

import lombok.*;

/**
 * @author malaka
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class GroupMemberDTO {

    private Long id;

    private Long userId;

    private Long groupId;

    private String nickname;

    private Integer type;//  类型，0成员，1管理员，2群主

}
