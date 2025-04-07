package com.li.chat.param.admin;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author malaka
 */
@Data
public class RoleCreateParam {

    @NotBlank(message = "角色名称不能为空")
    @Size(min = 2, max = 20, message = "角色名称长度必须在2-20之间")
    private String name;

    @NotBlank(message = "角色编码不能为空")
    @Size(min = 2, max = 20, message = "角色编码长度必须在2-20之间")
    private String code;

    private String description;

    private Integer sort;

    private List<Long> permissionIds;
}