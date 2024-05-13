package com.li.chat.param.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author malaka
 */
@ApiModel("Param好友申请同意")
@Data
public class FriendAgreeParam {

    @ApiModelProperty (value = "申请id", required = true)
    @NotNull(message = "申请Id不能为空")
    private Long id;

    @ApiModelProperty(value = "好友备注", required = true)
    @Length(min = 0,
            max = 24,
            message = "备注长度为0-24个字符")
    private String remark;

}
