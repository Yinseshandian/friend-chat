package com.li.chat.param.admin;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author malaka
 */

@Data
public class PermissionCreateParam {

    private Long parentId = 0L;

    @NotBlank(message = "权限名称不能为空")
    @Size(min = 2, max = 50, message = "权限名称长度必须在2-50之间")
    private String name;

    @NotBlank(message = "权限编码不能为空")
    @Size(min = 2, max = 50, message = "权限编码长度必须在2-50之间")
    private String code;

    private String path;

    private String component;

    private String redirect;

    private String icon;

    @NotNull(message = "权限类型不能为空")
    private Integer type; // 0-目录, 1-菜单, 2-按钮

    private Boolean hidden = false;

    private Boolean alwaysShow = false;

    private Boolean keepAlive = false;

    private Integer sort = 0;
}