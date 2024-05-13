package com.li.chat.param.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author malaka
 */
@ApiModel("Param好友申请")
@Data
public class FriendApplyParam {

    @ApiModelProperty(value = "对方id", required = true)
    @NotNull(message = "好友id不能为空")
    private Long toId;

    @ApiModelProperty(value = "验证消息", required = true)
    private String message;

    @ApiModelProperty(value = "好友备注", required = true)
    @Length(min = 0,
            max = 24,
            message = "备注长度为0-24个字符")
    private String remark;

}
