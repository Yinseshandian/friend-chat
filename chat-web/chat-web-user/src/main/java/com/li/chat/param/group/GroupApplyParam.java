package com.li.chat.param.group;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author malaka
 */
@ApiModel(value = "Param群聊加入申请")
@Data
public class GroupApplyParam {

    @ApiModelProperty(value = "对方id", required = true)
    @NotNull(message = "群号不能为空")
    private Long groupId;

    @ApiModelProperty(value = "验证消息", required = true)
    private String message;

}
