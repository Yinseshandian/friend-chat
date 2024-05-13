package com.li.chat.domain.DTO;

import lombok.*;

/**
 * @author malaka
 */

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class UserDTO {

    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String avatar;

    private String signature;

    private Integer sex; // 0：保密，1：男，2：女

    private Integer status; // 0：正常，1：冻结，2：注销


}
