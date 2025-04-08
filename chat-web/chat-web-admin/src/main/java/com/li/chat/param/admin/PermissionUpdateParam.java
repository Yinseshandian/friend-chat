package com.li.chat.param.admin;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author malaka
 */

@Data
public class PermissionUpdateParam {

    @NotNull(message = "权限ID不能为空")
    private Long id;

    private String name;

    private String code;

    private String path;

    private String component;

    private String redirect;

    private String icon;

    private Integer type;

    private Boolean hidden;

    private Boolean alwaysShow;

    private Boolean keepAlive;

    private Integer sort;

    private Boolean status;
}