package com.li.chat.vo.admin;

import com.li.chat.domain.admin.AdminDTO;
import com.li.chat.domain.admin.PermissionDTO;
import lombok.Data;

import java.util.List;

/**
 * @author malaka
 */
@Data
public class LoginVO {
    private String token;
    private AdminDTO admin;
    private List<PermissionDTO> permissions;
}