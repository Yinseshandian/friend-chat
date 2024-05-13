package com.li.chat.param.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * @author malaka
 */
@ApiModel("Param好友申请同意")
@Data
public class FriendUpdateRemarkParam {

    @ApiModelProperty (value = "好友id", required = true)
    @NotNull(message = "好友id不能为空")
    private Long friendId;

    @ApiModelProperty(value = "好友备注", required = true)
    @Length(min = 1,
            max = 24,
            message = "备注长度为1-24个字符")
    private String remark;

}
