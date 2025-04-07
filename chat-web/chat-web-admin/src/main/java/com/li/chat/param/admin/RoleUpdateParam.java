package com.li.chat.param.admin;


import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
/**
 * @author malaka
 */
@Data
public class RoleUpdateParam {

    @NotNull(message = "角色ID不能为空")
    private Long id;

    private String name;

    private String description;

    private Integer sort;

    private Boolean status;

    private List<Long> permissionIds;
}