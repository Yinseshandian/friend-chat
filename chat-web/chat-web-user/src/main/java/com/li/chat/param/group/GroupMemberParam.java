package com.li.chat.param.group;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author malaka
 */
@Data
public class GroupMemberParam {
    @NotNull
    @ApiModelProperty(value = "群id")
    private Long groupId;

    @NotNull
    @ApiModelProperty(value = "用户id")
    private Long userId;
}
