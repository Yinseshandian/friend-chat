package com.li.chat.vo.admin;

import com.li.chat.domain.admin.PermissionDTO;
import com.li.chat.domain.admin.RoleDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author malaka
 */
@Data
public class AdminInfoVO {
    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String mobile;
    private Boolean status;

    private LocalDateTime lastLoginTime;
    private LocalDateTime createTime;
    private List<RoleDTO> roles;
    private List<PermissionDTO> permissions;
}